package edu.zju.huyifeng.sportwear.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.zju.huyifeng.sportwear.R;

/**
 * Created by huyifeng on 15/3/10.
 */
public class FriendFragment extends Fragment {
    private View mRootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_friend, container, false);
        Bundle bundle = this.getArguments();
        initView(bundle);
        return mRootView;
    }

    private void initView(Bundle bundle) {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(1);
    }
}
