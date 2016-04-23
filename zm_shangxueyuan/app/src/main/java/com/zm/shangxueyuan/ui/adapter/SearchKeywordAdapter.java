package com.zm.shangxueyuan.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.model.KeywordModel;
import com.zm.shangxueyuan.ui.listener.OnItemClickListener;
import com.zm.shangxueyuan.utils.ResUtil;

import java.util.List;

/**
 * Creator: dengshengjin on 16/4/23 11:45
 * Email: deng.shengjin@zuimeia.com
 */
public class SearchKeywordAdapter extends BaseAdapter {
    private List<KeywordModel> mKeywordList;
    private Context mContext;

    public SearchKeywordAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        if (mKeywordList == null) {
            return 0;
        }
        int size = mKeywordList.size();
        return (int) Math.ceil(size / 2.0f);
    }

    @Override
    public Object getItem(int position) {
        return mKeywordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.search_keyword_item, null);

            for (int i = 0; i < 2; i++) {
                holder.view[i] = convertView.findViewById(ResUtil.getInstance(mContext).viewId("view" + i));
                holder.keywords[i] = (TextView) holder.view[i].findViewById(R.id.search_keyword_text);
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int start = position * 2;
        int end = start + 2;

        for (int i = start; i < end; i++) {
            final int pos = i % 2;
            if (i >= mKeywordList.size()) {
                holder.view[pos].setVisibility(View.INVISIBLE);
                holder.view[pos].setOnClickListener(null);
            } else {
                if (holder.view[pos].getVisibility() != View.VISIBLE) {
                    holder.view[pos].setVisibility(View.VISIBLE);
                }

                final KeywordModel keywordModel = mKeywordList.get(i);
                holder.keywords[pos].setText(keywordModel.getWord());

                holder.view[pos].setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(v, keywordModel, pos);
                        }

                    }
                });
            }
        }

        return convertView;
    }

    public void setKeywordList(List<KeywordModel> keywordList) {
        mKeywordList = keywordList;
    }

    static class ViewHolder {
        View view[] = new View[2];
        TextView keywords[] = new TextView[2];
    }


    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

}