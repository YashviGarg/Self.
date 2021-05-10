package com.example.selfhelp.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selfhelp.R;
import com.example.selfhelp.model.Journal;
import com.squareup.picasso.Picasso;

import java.util.List;

public class JournalRecyclerAdapter extends RecyclerView.Adapter<JournalRecyclerAdapter.Viewholder> {
    public Context context;
    private List<Journal> journalList;
    String title, thought;
    Uri uri;

    public JournalRecyclerAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.journal_list_item,parent,false);

        return new Viewholder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Journal journal = journalList.get(position);
        String imageUrl = journal.getImageUrl();

        holder.title.setText(journal.getTitle());
        holder.thought.setText(journal.getThoughts());

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.draw2)
                .fit()
                .into(holder.image);

        String date = (String) DateUtils.getRelativeTimeSpanString(journal.getTimeAdded().getSeconds()*1000);
        holder.date.setText(date);
        holder.userName.setText(journal.getUsername());
        title =journal.getTitle();
        thought= journal.getThoughts();
        uri = Uri.parse(imageUrl);
    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title,thought,date;
        public TextView userName;
        public ImageView image,shareButton;
        public Viewholder(@NonNull View itemView, final Context context) {
            super(itemView);


            title = itemView.findViewById(R.id.title_TextView_list);
            thought = itemView.findViewById(R.id.thought_TextView_list);
            date = itemView.findViewById(R.id.time_TextView_list);
            image = itemView.findViewById(R.id.imageViewList);
            userName = itemView.findViewById(R.id.nameJournalListItem);
            shareButton = itemView.findViewById(R.id.shareButton);

            shareButton.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_SUBJECT,"Title: " + title.getText().toString().trim());
            intent.putExtra(Intent.EXTRA_TEXT,"Thougtht: " + thought.getText().toString().trim() );
            intent.setData(uri);
            context.startActivity(intent);

        }
    }

}
