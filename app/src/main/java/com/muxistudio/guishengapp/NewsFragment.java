package com.muxistudio.guishengapp;


import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NewsFragment extends Fragment {
    static View list_view;
    static MyListView news_listview;
    static ListViewAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        list_view = LayoutInflater.from(getActivity()).inflate(R.layout.news,null);
        adapter = new ListViewAdapter(getActivity(),Api.news_list);
        news_listview = (MyListView) list_view.findViewById(R.id.news_listview);
        news_listview.setAdapter(adapter);
        news_listview.adapter = adapter;
        news_listview.setOnRefreshListener(new MyListView.OnRefreshListener() {
            @Override
            public void onHeaderRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    int header_refresh_state;
                    protected Void doInBackground(Void... params) {
                        long beginTime = System.currentTimeMillis();
                        if (NetDataObtain.isNetworkAvailable(getActivity()))
                            header_refresh_state = new NetDataObtain(getContext()).DataRequireOver(0);
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
                            news_listview.refresh();
                        else if (header_refresh_state == 1)
                            news_listview.alreadyRefreshed();
                        else if (header_refresh_state == -1)
                            news_listview.refreshFail();
                        news_listview.rollback(0);
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
                            footer_refresh_state = new NetDataObtain(getContext()).DataRequireAppend(0);
                        else
                            footer_refresh_state = -1;
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
                            news_listview.setFooter_is_newest();
                        else if (footer_refresh_state == -1)
                            news_listview.setFooter_fail_load();
                        new AsyncTask<Void, Void, Void>() {
                            protected Void doInBackground(Void... params) {
                                try {
                                    Thread.sleep(1100);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                            @Override
                            protected void onPostExecute(Void result) {
                                news_listview.rollback(1);
                            }
                        }.execute(null, null, null);
                    }
                }.execute(null, null, null);
            }
        });
        return list_view;
    }

}
