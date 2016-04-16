package com.zm.shangxueyuan.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.model.NavModel;

import java.util.List;

public class NavAdapter extends BaseAdapter {
    private List<NavModel> navList;
    private Context context;
    private int mItemHeight;

    public NavAdapter(Context context, int itemHeight) {
        super();
        this.context = context;
        mItemHeight = itemHeight;
    }

    @Override
    public int getCount() {
        if (navList == null) {
            return 0;
        }
        return navList.size();
    }

    @Override
    public NavModel getItem(int position) {
        return navList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NavModel model = navList.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.view_home_menu_item, null);
            holder = new ViewHolder();
            initHolder(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (holder == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.view_home_menu_item, null);
            holder = new ViewHolder();
            initHolder(holder, convertView);
            convertView.setTag(holder);
        }
        updateHolder(holder, model);
        return convertView;
    }

    private void initHolder(ViewHolder holder, View view) {
        holder.setMenuText((TextView) view.findViewById(R.id.menu_item_text));
        holder.mContent = (LinearLayout) view.findViewById(R.id.menu_content);
    }

    private void updateHolder(ViewHolder holder, NavModel model) {
        holder.getMenuText().setText(model.getTitle());
        if (model.getNavId() == 1) {
            holder.getMenuText().setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_01, 0, 0);
        } else if (model.getNavId() == 2) {
            holder.getMenuText().setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_02, 0, 0);
        } else if (model.getNavId() == 3) {
            holder.getMenuText().setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_03, 0, 0);
        } else if (model.getNavId() == 4) {
            holder.getMenuText().setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_04, 0, 0);
        } else if (model.getNavId() == 5) {
            holder.getMenuText().setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_05, 0, 0);
        } else if (model.getNavId() == 6) {
            holder.getMenuText().setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_06, 0, 0);
        } else if (model.getNavId() == 7) {
            holder.getMenuText().setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_07, 0, 0);
        }
        GridView.LayoutParams lp = (GridView.LayoutParams) holder.mContent.getLayoutParams();
        if (lp == null) {
            lp = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, mItemHeight);
            holder.mContent.setLayoutParams(lp);
        } else {
            lp.height = mItemHeight;
            holder.mContent.requestLayout();
        }


    }

    public List<NavModel> getNavList() {
        return navList;
    }

    public void setNavList(List<NavModel> navList) {
        this.navList = navList;
    }

    final class ViewHolder {
        private TextView menuText;
        LinearLayout mContent;

        public TextView getMenuText() {
            return menuText;
        }

        public void setMenuText(TextView menuText) {
            this.menuText = menuText;
        }

    }

}
