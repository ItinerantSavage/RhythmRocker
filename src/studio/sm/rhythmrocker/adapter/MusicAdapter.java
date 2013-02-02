package studio.sm.rhythmrocker.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import studio.sm.rhythmrocker.ui.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicAdapter extends BaseAdapter
{
	private Context context;
	private LayoutInflater mInflater;
	private int selectedItem = -1; 
	private ArrayList<HashMap<String,Object>> mData;
	
	//--> constructor
	public MusicAdapter(Context context, ArrayList<HashMap<String,Object>> mData)
	{
		this.context = context;
		this.mInflater = LayoutInflater.from(this.context);
		this.mData = mData;
	}
	
    public void setSelectedItem(int selectedItem)
    {  
        this.selectedItem = selectedItem;  
    } 
    
	@Override
	public int getCount()
	{
		return this.mData.size();
	}

	@Override
	public Object getItem(int position)
	{
		return mData.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder = null;  
        if (convertView == null)
        {  
            holder = new ViewHolder();  
            convertView = mInflater.inflate(R.layout.listview_music, null);  
            holder.textView_track_index = (TextView) convertView.findViewById(R.id.textView_track_index);  
            holder.textView_track_name = (TextView) convertView.findViewById(R.id.textView_track_name);  
            holder.imageView_track_icon = (ImageView) convertView.findViewById(R.id.imageView_track_icon);  
              
            convertView.setTag(holder);           
        } 
        else
        {  
            holder = (ViewHolder) convertView.getTag();  
        }  
        holder.textView_track_index.setText((String) mData.get(position).get("musicTrackIndex"));  
        holder.textView_track_name.setText((String) mData.get(position).get("musicTitle"));  
        holder.imageView_track_icon.setVisibility(View.INVISIBLE);
        
        if(position == selectedItem && selectedItem != -2)
        {  
            convertView.setBackgroundColor(0xFFCCCCCC);
//            convertView.setBackgroundResource(R.color.selected);
            holder.imageView_track_icon.setVisibility(View.VISIBLE);
            holder.textView_track_index.setTextAppearance(context, R.style.PlayingTrackNameFontStyle);
            holder.textView_track_name.setTextAppearance(context, R.style.PlayingTrackNameFontStyle);
            
        }   
        else
        {  
//        	convertView.setBackgroundResource(R.color.unselected);
        	convertView.setBackgroundColor(Color.TRANSPARENT);
        	holder.textView_track_index.setTextAppearance(context, R.style.NotPlayingTrackNameFontStyle);
        	holder.textView_track_name.setTextAppearance(context, R.style.NotPlayingTrackNameFontStyle);
        }
        //convertView.getBackground().setAlpha(80);   
          
        return convertView;  
	}
	
    private final static class ViewHolder
    {  
    	public ImageView imageView_track_icon;
    	public TextView textView_track_index;  
        public TextView textView_track_name;  
    }//--> End of class ViewHolder

}
