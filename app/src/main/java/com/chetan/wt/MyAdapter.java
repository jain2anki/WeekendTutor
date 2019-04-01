package com.chetan.wt;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
{

    class MyViewHolder extends RecyclerView.ViewHolder
    {

        public TextView tut,cour,ti,da;
        public ImageView iv;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tut = (TextView) itemView.findViewById(R.id.tutor_name);
            cour = (TextView) itemView.findViewById(R.id.course_name);
            da = (TextView) itemView.findViewById(R.id.class_date);
            ti = (TextView) itemView.findViewById(R.id.time_duration);
            iv = (ImageView) itemView.findViewById(R.id.tutorImage);
        }
    }

    ArrayList<Course> Courses;

    public MyAdapter(ArrayList<Course> c)
    {
        Courses = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {

        Course course = Courses.get(i);
        myViewHolder.tut.setText(course.getTname());
        myViewHolder.cour.setText(course.getName());
        myViewHolder.da.setText(course.getDate() + "\t" + course.getVenue());
        myViewHolder.ti.setText(course.getStart() + "\t" + course.getDuration());
        String tid = course.getTId().toString();
        DatabaseReference ref;
        ref = FirebaseDatabase.getInstance().getReference("users").child(tid).child("durl");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                Picasso.get().load(url).fit().centerCrop().error(R.drawable.noimage).into(myViewHolder.iv);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                myViewHolder.iv.setImageResource(R.drawable.noimage);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Courses.size();
    }






//    String tid;
//    StorageReference sr;
//    ViewHolder mViewHolder = new ViewHolder();
//    DatabaseReference ref;
//    String url;
//    ArrayList<String> tutorList,courseList,dateList,durationList,timeList, tidList;
//    Context mContext;
//    public MyAdapter(Context context, ArrayList<String> tutorName, ArrayList<String> courseName, ArrayList<String> date, ArrayList<String> duration, ArrayList<String> time, ArrayList<String> tidList) {
//        super(context, R.layout.list_item);
//        this.tutorList = tutorName;
//        this.courseList = courseName;
//        this.dateList = date;
//        this.durationList = duration;
//        this.timeList = time;
//        this.tidList = tidList;
//        this.mContext=context;
//    }
//
//    @Override
//    public int getCount() {
//        return tutorList.size();
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position,View convertView,ViewGroup parent)
//    {
//        /*LayoutInflater mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view  = mInflator.inflate(R.layout.list_item,null,true);
//
//        TextView tutorName = (TextView) view.findViewById(R.id.tutor_name);
//        TextView courseName = (TextView) view.findViewById(R.id.course_name);
//        TextView date = (TextView) view.findViewById(R.id.class_date);
//        TextView time_duration = (TextView) view.findViewById(R.id.time_duration);
//        final ImageView imageView = (ImageView) view.findViewById(R.id.tutorImage);
//        imageView.setImageResource(R.drawable.loading);
//
//        tutorName.setText(tutorList.get(position));
//        courseName.setText(courseList.get(position));
//        date.setText(dateList.get(position));
//        time_duration.setText(timeList.get(position)+"   "+durationList.get(position));
//        tid=tidList.get(position);
//        ref = FirebaseDatabase.getInstance().getReference("users").child(tid).child("durl");
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                url = dataSnapshot.getValue(String.class);
//                Picasso.get().load(url).fit().centerCrop().error(R.drawable.noimage).into(imageView);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                imageView.setImageResource(R.drawable.noimage);
//            }
//        });
//
//        return view;*/
//        if(convertView==null)
//        {
//            LayoutInflater mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = mInflator.inflate(R.layout.list_item, parent, false);
//            mViewHolder.imageView = (ImageView) convertView.findViewById(R.id.tutorImage);
//            mViewHolder.tutorName = (TextView) convertView.findViewById(R.id.tutor_name);
//            mViewHolder.courseName = (TextView) convertView.findViewById(R.id.course_name);
//            mViewHolder.date = (TextView) convertView.findViewById(R.id.class_date);
//            mViewHolder.time_duration = (TextView) convertView.findViewById(R.id.time_duration);
//            convertView.setTag(mViewHolder);
//        }else
//        {
//            mViewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        mViewHolder.tutorName.setText(tutorList.get(position));
//        mViewHolder.courseName.setText(courseList.get(position));
//        mViewHolder.date.setText(dateList.get(position));
//        mViewHolder.time_duration.setText(timeList.get(position)+"   "+durationList.get(position));
//        //mViewHolder.imageView.setImageResource(R.drawable.jelwin);
//        tid=tidList.get(position);
//        ref = FirebaseDatabase.getInstance().getReference("users").child(tid).child("durl");
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                url = dataSnapshot.getValue(String.class);
//                Picasso.get().load(url).fit().centerCrop().error(R.drawable.noimage).into(mViewHolder.imageView);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                mViewHolder.imageView.setImageResource(R.drawable.noimage);
//            }
//        });
//
//
//        return convertView;
//    }



//    static class ViewHolder
//    {
//        ImageView imageView;
//        TextView tutorName;
//        TextView courseName;
//        TextView date;
//        TextView time_duration;
//    }
}
