package com.example.patrick.netnix.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.patrick.netnix.Cache;
import com.example.patrick.netnix.R;
import com.example.patrick.netnix.SerializationUtil;
import com.example.patrick.netnix.Util;
import com.example.patrick.netnix.models.Show;
import com.example.patrick.netnix.services.ApiService;

import java.io.IOException;

import me.grantland.widget.AutofitHelper;


public class ShowDetailFragment extends Fragment implements AsyncListener {

    private TextView mTextMessage;
    private TextView mStatus;
    private TextView mGenres;
    private TextView mNetwork;
    private Button mFollow;
    private Button mCheckEpisodes;
    private RatingBar mRating;
    private ImageView mImage;
    private WebView mSummary;
    private Show mShow;

    private final AsyncListener mSelf = this;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_show_detail,
                container, false);

        mShow = null;
        Bundle args = getArguments();
        if (args != null) {
            mShow = args.getParcelable("show");
        }

        mTextMessage = (TextView) view.findViewById(R.id.title);
        mStatus = (TextView) view.findViewById(R.id.status);
        mGenres = (TextView) view.findViewById(R.id.genres);
        mNetwork = (TextView) view.findViewById(R.id.network);
        mFollow = (Button) view.findViewById(R.id.followBtn);
        mCheckEpisodes = (Button) view.findViewById(R.id.check_episodes);
        mRating = (RatingBar) view.findViewById(R.id.rating);
        mImage = (ImageView) view.findViewById(R.id.image);
        mSummary = (WebView) view.findViewById(R.id.summary);

        AutofitHelper.create(mTextMessage);
        AutofitHelper.create(mGenres);

        if (mShow == null) {
            mTextMessage.setText(getActivity().getString(R.string.unable_to_load_show_data));
            return view;
        }

        mTextMessage.setText(mShow.getName());
        mStatus.setText(getActivity().getString(R.string.show_status, mShow.getStatus()));
        mNetwork.setText(mShow.getNetwork());
        mGenres.setText(mShow.getGenres());
        mRating.setRating(mShow.getRating());

        mSummary.loadData(mShow.getSummary(), "text/html; charset=utf-8", "utf-8");

        setFollowView(mShow.isFollowed(getActivity()));
        mCheckEpisodes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    EpisodesFragment sdf = new EpisodesFragment();
                    Bundle b = new Bundle();
                    b.putParcelable("show", mShow);
                    sdf.setArguments(b);
                    getFragmentManager().beginTransaction().replace(R.id.content, sdf, "episodes").addToBackStack(null).commit();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        });

        mFollow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mShow.isFollowed(getActivity())) {
                    mShow.unfollow(getActivity(), getContext());
                    setFollowView(false);
                } else {
                    mShow.follow(getActivity(), getContext(), mSelf);
                    setFollowView(true);
                    mCheckEpisodes.setVisibility(View.VISIBLE);
                }
            }
        });

        if (mShow.getImageURL() == null) {
            mImage.setImageBitmap(Util.getDefaultImage(getContext()));
        } else {
            // Get the image asynchronously through the ImageLoader.
            ApiService.getInstance(getContext()).getImageLoader().get(mShow.getImageURL(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        mImage.setImageBitmap(response.getBitmap());
                    }
                }
                @Override
                public void onErrorResponse(VolleyError error) {
                    mImage.setImageBitmap(Util.getDefaultImage(getContext()));
                }
            });
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("show", mShow);
    }

    public void setFollowView(boolean status) {

        if (status) {
            mCheckEpisodes.setEnabled(true);
            mCheckEpisodes.setVisibility(View.VISIBLE);
            mCheckEpisodes.setAlpha(0.0f);

            // Start the animation
            mCheckEpisodes.animate().alpha(1.0f);
        } else {
            mCheckEpisodes.setEnabled(false);
            mCheckEpisodes.setVisibility(View.VISIBLE);
            mCheckEpisodes.setAlpha(1.0f);

            // Start the animation
            mCheckEpisodes.animate().alpha(0.0f);
            mCheckEpisodes.setVisibility(View.GONE);
        }

        mFollow.setText(status ? getActivity().getString(R.string.unfollow) : getActivity().getString(R.string.follow));
        mFollow.setBackgroundColor(status ? ContextCompat.getColor(getContext(), R.color.colorDecline) : ContextCompat.getColor(getContext(), R.color.colorAccept));
        mFollow.setCompoundDrawablesWithIntrinsicBounds(status ? ContextCompat.getDrawable(getContext(), R.drawable.ic_remove_from_queue_white_24dp) : ContextCompat.getDrawable(getContext(), R.drawable.ic_add_to_queue_white_24dp), null, null, null);
    }

    @Override
    public void callback(Object o) {
        if (o != null) {
            if (o instanceof Show) {
                Show s = (Show) o;
                ((Cache) this.getActivity().getApplication()).getMyShows().add(s);
                s.saveToSharedPreference(getContext());
                Util.showToast(getActivity().getString(R.string.follow_message, s.getName(), (s.getTotalEpisodes() - s.getWatchedEpisodes())), getActivity());
            }
            else if (o instanceof String) {
                String id = (String) o;
                try {
                    Show s = SerializationUtil.deserialize(id, getContext());
                    ((Cache) this.getActivity().getApplication()).getMyShows().add(s);
                    Util.showToast(getActivity().getString(R.string.follow_message, s.getName(), (s.getTotalEpisodes() - s.getWatchedEpisodes())), getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
