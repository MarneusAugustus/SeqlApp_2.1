package adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.luis.qrscannerfrandreas.App;
import com.example.luis.qrscannerfrandreas.BoxInfo;
import com.example.luis.qrscannerfrandreas.R;

import java.util.List;

import modal.Scans;
import sql.DatabaseHelper;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.UserViewHolder> {

    public static int boxid;
    DatabaseHelper databaseHelper;
    Context context;
    int position;
    private List<Scans> listScans;

    public UsersRecyclerAdapter(List<Scans> listUsers) {
        this.listScans = listUsers;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflating recycler item view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scan_recycler, parent, false);
        context = App.getContext();
        return new UserViewHolder(itemView);
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        databaseHelper = new DatabaseHelper(App.getContext());
        holder.textViewName.setText(listScans.get(position).getBoxID() + " - " + listScans.get(position).getName());
        holder.textViewExptime.setText(listScans.get(position).getExptime());
        holder.textViewDate.setText(listScans.get(position).getDate());

        if (databaseHelper.checkIfAlreadyScan()) {

            if (listScans.get(position).getStatus() == 1 && listScans.get(position).getTiming() == 1) {
                holder.cardview.setCardBackgroundColor(Color.rgb(0xFE, 0xDE, 0x0A));
                holder.textViewName.setTextColor(BLACK);
                holder.textViewExptime.setTextColor(BLACK);
                holder.textViewDate.setTextColor(BLACK);

            } else if (listScans.get(position).getStatus() == 2 && listScans.get(position).getTiming() == 1) {
                holder.cardview.setCardBackgroundColor(Color.rgb(0xFE, 0xDE, 0x0A));
                holder.textViewName.setTextColor(BLACK);
                holder.textViewExptime.setTextColor(BLACK);
                holder.textViewDate.setTextColor(BLACK);

            } else if (listScans.get(position).getStatus() == 2 && listScans.get(position).getTiming() == 2) {
                holder.cardview.setCardBackgroundColor(Color.rgb(0x00, 0xC0, 0x00));
                holder.textViewName.setTextColor(BLACK);
                holder.textViewExptime.setTextColor(BLACK);
                holder.textViewDate.setTextColor(BLACK);

            }else if (listScans.get(position).getStatus() == 1 && listScans.get(position).getTiming() == 2) {
                holder.cardview.setCardBackgroundColor(Color.rgb(0x00, 0xC0, 0x00));
                holder.textViewName.setTextColor(BLACK);
                holder.textViewExptime.setTextColor(BLACK);
                holder.textViewDate.setTextColor(BLACK);

            } else {
                holder.cardview.setCardBackgroundColor(Color.rgb(0xc0, 0x00, 0x00));
                holder.textViewName.setTextColor(WHITE);
                holder.textViewExptime.setTextColor(WHITE);
                holder.textViewDate.setTextColor(WHITE);
            }

        } else {
            holder.cardview.setCardBackgroundColor(Color.rgb(0x69, 0x69, 0x69));
            holder.textViewName.setTextColor(WHITE);
            holder.textViewExptime.setTextColor(WHITE);
            holder.textViewDate.setTextColor(WHITE);
        }
    }

    @Override
    public int getItemCount() {
        Log.v(UsersRecyclerAdapter.class.getSimpleName(), "" + listScans.size());
        Log.i("BOXUPDATE debug", "ListenAnzahl: " + listScans.size());

        return listScans.size();
    }

    /**
     * ViewHolder class
     */
    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public AppCompatTextView textViewName;
        public AppCompatTextView textViewExptime;
        public AppCompatTextView textViewDate;
        public ImageView imageViewStatus;
        public CardView cardview;

        public UserViewHolder(View view) {
            super(view);
            textViewName = view.findViewById(R.id.textViewName);
            textViewExptime = view.findViewById(R.id.textViewExptime);
            textViewDate = view.findViewById(R.id.textViewDate);
            imageViewStatus = view.findViewById(R.id.imageViewStatus);
            cardview = view.findViewById(R.id.cardview);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            position = getAdapterPosition();
            boxid = listScans.get(position).getBoxID();
            Intent boxInfoIntent = new Intent(context, BoxInfo.class);
            context.startActivity(boxInfoIntent);
        }

    }
}
