package com.coursera.marcossastre.dailyselfie;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcos Sastre on 09/11/2015.
 */
public class SelfieListAdapter extends BaseAdapter {

    private static final String TAG = "DailySelfie-ListAdapter";
    private final List<SelfieItem> mItems = new ArrayList<SelfieItem>();
    private final Context mContext;



    //Constructor
    public SelfieListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    //Add selfie item
    public void add(SelfieItem item){
        mItems.add(item);
        notifyDataSetChanged();
    }

    //Clears a Selfie at a given position
     public void clear(int pos){
         mItems.remove(pos);
         notifyDataSetChanged();
     }

    //Clear all selfies
    public void clearAll(){
        int countBeforeDeleting = getCount();
        for (int i = 0; i < countBeforeDeleting ; i++){
            final SelfieItem currentSelfie = (SelfieItem) getItem(0);
            File deleted = new File (currentSelfie.getFullImagePath());
            clear(0);
            boolean result = deleted.delete();
            }

    }

    //Methods that must be overridden from the Interface
    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    //Creates a View for the selfie at the specified position
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //Get the current Selfie Item
        final SelfieItem currentSelfie = (SelfieItem) getItem(position);

        //In order to recycle the View if it is passed, only inflates convertView
        //if it is null
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)
                    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.selfie_item, null);
        }

        //Reference to the layout
        RelativeLayout selfieLayout = (RelativeLayout) convertView.findViewById(R.id.selfieLayout);

        //Reference to the ImageView
        ImageView thumb = (ImageView) convertView.findViewById(R.id.thumb);
        //Set the thumb
        thumb.setImageBitmap(currentSelfie.getThumb());

        //Reference to the TextView
        TextView title = (TextView) convertView.findViewById(R.id.title);
        //Set the title
        title.setText(currentSelfie.getTitle());


        //Reference to the Button
       ImageButton removeButton = (ImageButton) convertView.findViewById(R.id.removeButton);
        //set OnClickListener to remove the current selfie when clicked
       removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear(position);
                File deleted = new File (currentSelfie.getFullImagePath());
                boolean result = deleted.delete();
                Log.i(TAG, "Result of the deletion: " + String.valueOf(result));
            }
        });

        //return the just created View
        return selfieLayout;
    }
}
