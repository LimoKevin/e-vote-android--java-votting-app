package com.company.evote;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.smarteist.autoimageslider.SliderViewAdapter;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder> {

    int [] images;
    String [] title1;
    String [] desc;
    String [] date;

    public SliderAdapter(int [] images,String [] title1 ,String [] desc,String [] date){
        this.images = images;
        this.title1 = title1;
        this.desc = desc;
        this.date = date;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single, parent, false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {

        viewHolder.imageView.setBackgroundResource(images[position]);
        viewHolder.tittle1.setText(title1[position]);
        viewHolder.desc.setText(desc[position]);
        viewHolder.date.setText(date[position]);


    }

    @Override
    public int getCount() {
        return images.length ;
    }

    public class Holder extends SliderViewAdapter.ViewHolder{

        ConstraintLayout imageView;
        TextView tittle1, desc, date;

        public Holder(View itemView){
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view);
            tittle1 = itemView.findViewById(R.id.tittle);
            desc = itemView.findViewById(R.id.des);
            date = itemView.findViewById(R.id.date);

        }

    }
}
