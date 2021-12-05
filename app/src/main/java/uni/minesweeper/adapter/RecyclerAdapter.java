package uni.minesweeper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import uni.minesweeper.R;
import uni.minesweeper.UserClass;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
  private final List<UserClass> mData;
  private final LayoutInflater mInflater;

  public RecyclerAdapter(Context context, List<UserClass> data) {
    this.mInflater = LayoutInflater.from(context);
    this.mData = data;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    UserClass user = mData.get(position);
    holder.ranking.setText((position + 1) + ".");
    holder.username.setText(user.getEmail());
    holder.score.setText(user.getScore());
  }

  @Override
  public int getItemCount() {
    return mData.size();
  }


  public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView ranking, username, score;

    ViewHolder(View itemView) {
      super(itemView);
      ranking = itemView.findViewById(R.id.tvRank);
      username = itemView.findViewById(R.id.tvUsername);
      score = itemView.findViewById(R.id.tvScore);
    }
  }

}
