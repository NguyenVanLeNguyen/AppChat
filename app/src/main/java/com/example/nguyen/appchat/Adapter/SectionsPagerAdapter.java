package com.example.nguyen.appchat.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.nguyen.appchat.Fragment.ChatsFragment;
import com.example.nguyen.appchat.Fragment.FriendsFragment;
import com.example.nguyen.appchat.Fragment.GroupFragment;
import com.example.nguyen.appchat.Fragment.RequestFragment;


public class SectionsPagerAdapter extends FragmentPagerAdapter{

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return  chatsFragment;

            case 1:
                RequestFragment requestsFragment = new RequestFragment();
                return requestsFragment;

            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            case 3:
                GroupFragment groupFragment = new GroupFragment();
                return groupFragment;
            default:
                return  null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    public CharSequence getPageTitle(int position){

        switch (position) {
            case 0:
                return "CHAT";

            case 1:
                return "REQUEST";

            case 2:
                return "FRIEND";
            case 3:
                return "GROUP";

            default:
                return null;
        }

    }
}
