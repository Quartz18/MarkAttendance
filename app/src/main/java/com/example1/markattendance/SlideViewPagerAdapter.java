package com.example1.markattendance;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class SlideViewPagerAdapter extends PagerAdapter {
    Context context;

    public SlideViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_screen,container,false);
        ImageView logo = view.findViewById(R.id.imageView);
        ImageView indicator1 = view.findViewById(R.id.indicator1);
        ImageView indicator2 = view.findViewById(R.id.indicator2);
        ImageView indicator3 = view.findViewById(R.id.indicator3);
        TextView title = view.findViewById(R.id.title);
        TextView desc = view.findViewById(R.id.desc);
        Button btngetstarted = view.findViewById(R.id.btngetstarted);
        Animation btnanim = AnimationUtils.loadAnimation(context,R.anim.btn_get_started_anim);
        btngetstarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        switch (position)
        {
            case 0:
                logo.setImageResource(R.drawable.image1);
                indicator1.setImageResource(R.drawable.indicator_selected);
                indicator2.setImageResource(R.drawable.indicator_unselected);
                indicator3.setImageResource(R.drawable.indicator_unselected);
                title.setText("Class, Student and" +
                        " Subject");
                desc.setText("You can see the list of class, students and subjects." +
                        "You can add, edit and delete then anytime!");
                break;
            case 1:
                logo.setImageResource(R.drawable.image2);
                indicator1.setImageResource(R.drawable.indicator_unselected);
                indicator2.setImageResource(R.drawable.indicator_selected);
                indicator3.setImageResource(R.drawable.indicator_unselected);
                title.setText("Attendance and" +
                        " Records");
                desc.setText("You can see the list of past records, take attendance of students." +
                        "You can also download the csv file of the records anytime!");
                break;
            case 2:
                logo.setImageResource(R.drawable.image3);
                indicator1.setImageResource(R.drawable.indicator_unselected);
                indicator2.setImageResource(R.drawable.indicator_unselected);
                indicator3.setImageResource(R.drawable.indicator_selected);
                title.setText("Performance and" +
                        " Statistics");
                btngetstarted.setVisibility(View.VISIBLE);
                btngetstarted.setEnabled(true);
                btngetstarted.setAnimation(btnanim);
                desc.setText("You can see the performance of any student and analyze their performance through graph." +
                        "Enjoy your Day!");
                break;
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
