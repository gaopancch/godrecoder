package com.gaopan.godrecoder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.gaopan.godrecoder.R;
import com.gaopan.godrecoder.Utils.ConstantUtils;
import com.gaopan.godrecoder.Utils.PreferenceUtil;
import com.gaopan.godrecoder.Utils.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by gaopan on 2017/6/29.
 */

public class ShowListAdapter extends BaseAdapter {
    private  ArrayList<String> iRecordFiles;
    private LayoutInflater mInflater=null;
    private Context context;
    private ArrayList<String> markList;
    public ShowListAdapter(Context context,ArrayList<String> iRecordFiles){
        this.context=context;
        mInflater=LayoutInflater.from(context);
        this.iRecordFiles=iRecordFiles;
        markList=PreferenceUtil.getMakrList(context);
    }
    @Override
    public int getCount() {
        return iRecordFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return iRecordFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final boolean isMarked=markList.contains(iRecordFiles.get(position));
        ViewHolder holder=null;
        if(convertView==null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.recoder_item_base_layout, null);
            holder.name=(TextView)convertView.findViewById(R.id.arm_name);
            holder.markButton =(Button)convertView.findViewById(R.id.makr_button);
            holder.deleteButton=(Button)convertView.findViewById(R.id.delete_button);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag(); //这里是为了提高listview的运行效率
        }
        holder.name.setText(iRecordFiles.get(position));
        final Button button=holder.markButton;
        holder.markButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File iRecAudioDirMarked= new File(ConstantUtils.RECODER_FILE_SAVE_MARKED_PATH);
                if (!iRecAudioDirMarked.exists()) {
                    iRecAudioDirMarked.mkdirs();
                }
                if(isMarked){
                    PreferenceUtil.deleteMakrData(context,iRecordFiles.get(position));
                    Utils.copyFile(ConstantUtils.RECODER_FILE_SAVE_MARKED_PATH+File.separator+iRecordFiles.get(position),
                            ConstantUtils.RECODER_FILE_SAVE_PATH+File.separator+iRecordFiles.get(position));
                    button.setText("标记");
                }else{
                    PreferenceUtil.makrData(context,iRecordFiles.get(position));
                    Utils.copyFile(ConstantUtils.RECODER_FILE_SAVE_PATH+File.separator+iRecordFiles.get(position),
                            ConstantUtils.RECODER_FILE_SAVE_MARKED_PATH+File.separator+iRecordFiles.get(position));
                    button.setText("已标记");
                }
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file=new File (ConstantUtils.RECODER_FILE_SAVE_PATH+File.separator+iRecordFiles.get(position));
                file.delete();
                PreferenceUtil.deleteMakrData(context,iRecordFiles.get(position));
                iRecordFiles.remove(position);
                notifyDataSetChanged();
            }
        });
        if(isMarked) {
            holder.markButton.setText("已标记");
        }else{
            holder.markButton.setText("标记");
        }
        return convertView;
    }

   private  class ViewHolder
    {
        public TextView name;
        public Button markButton;
        public Button deleteButton;
    }
}
