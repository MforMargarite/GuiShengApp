package com.muxistudio.guishengapp;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class InteractFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View list_view = inflater.inflate(R.layout.interact, container, false);
        ListViewAdapter adapter =new ListViewAdapter(getActivity(),Api.interact_list);
        final MyListView interact_listview = (MyListView)list_view.findViewById(R.id.interact_listview);
        interact_listview.setAdapter(adapter);
        interact_listview.adapter = adapter;
        interact_listview.which_tab = 2;
        list_view.setTag(2);
        interact_listview.setOnRefreshListener(new MyListView.OnRefreshListener() {
            @Override
            public void onHeaderRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    int header_refresh_state;
                    protected Void doInBackground(Void... params) {
                        if (NetDataObtain.isNetworkAvailable(getActivity()))
                            header_refresh_state = new NetDataObtain().DataRequireOver();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        if (header_refresh_state == 0)
                            interact_listview.refresh();
                        else if (header_refresh_state == 1)
                            interact_listview.alreadyRefreshed();
                        else if (header_refresh_state == -1)
                            interact_listview.refreshFail();
                        interact_listview.rollback(0);
                    }
                }.execute(null, null, null);
            }

            @Override
            public void onFooterRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    int footer_refresh_state;
                    protected Void doInBackground(Void... params) {
                        long beginTime = System.currentTimeMillis();
                        if (NetDataObtain.isNetworkAvailable(getActivity()))
                            footer_refresh_state = new NetDataObtain().DataRequireAppend();
                        long endTime = System.currentTimeMillis();
                        if (endTime - beginTime < 2000)
                            try {
                                Thread.sleep(2000 - (endTime - beginTime));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        if (footer_refresh_state == 1)
                            interact_listview.setFooter_is_newest();
                        else if (footer_refresh_state == -1)
                            interact_listview.setFooter_fail_load();
                        new AsyncTask<Void, Void, Void>() {
                            protected Void doInBackground(Void... params) {
                                try {
                                    Thread.sleep(1200);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void result) {
                                interact_listview.rollback(1);
                            }
                        }.execute(null, null, null);
                    }
                }.execute(null, null, null);
            }
        });
        return list_view;
        }
}