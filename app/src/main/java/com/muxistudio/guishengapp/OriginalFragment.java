package com.muxistudio.guishengapp;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class OriginalFragment extends Fragment {
    static View list_view ;
    static MyListView original_listview;
    static ListViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        list_view =inflater.inflate(R.layout.original, container, false);
        adapter =new ListViewAdapter(getActivity(),Api.original_list);
        original_listview = (MyListView)list_view.findViewById(R.id.original_listview);
        original_listview.setAdapter(adapter);
        original_listview.adapter = adapter;
        original_listview.setOnRefreshListener(new MyListView.OnRefreshListener() {
            @Override
            public void onHeaderRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    int header_refresh_state;
                    protected Void doInBackground(Void... params) {
                        long beginTime = System.currentTimeMillis();
                        if (NetDataObtain.isNetworkAvailable(getActivity()))
                            header_refresh_state = new NetDataObtain(getContext()).DataRequireOver(1);
                        long endTime = System.currentTimeMillis();
                        if (endTime - beginTime < 2000)
                            try {
                                Thread.sleep(2000 - (endTime - beginTime));
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        if (header_refresh_state == 0)
                            original_listview.refresh();
                        else if (header_refresh_state == 1)
                            original_listview.alreadyRefreshed();
                        else if (header_refresh_state == -1)
                            original_listview.refreshFail();
                        original_listview.rollback(0);
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
                            footer_refresh_state = new NetDataObtain(getContext()).DataRequireAppend(1);
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
                            original_listview.setFooter_is_newest();
                        else if (footer_refresh_state == -1)
                            original_listview.setFooter_fail_load();
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
                                original_listview.rollback(1);
                            }
                        }.execute(null, null, null);
                    }
                }.execute(null, null, null);
            }
        });
        return list_view;
    }
}
