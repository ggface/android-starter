package orwir.starter.ui;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import orwir.starter.R;
import orwir.starter.logic.model.Article;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ArticleHolder> {

    private static final ThreadLocal<DateFormat> DATE = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        }
    };

    private List<Article> news = Collections.emptyList();

    @Override
    public ArticleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ArticleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_article, parent, false));
    }

    @Override
    public void onBindViewHolder(ArticleHolder holder, int position) {
        Article article = news.get(position);
        holder.title.setText(article.getTitle());
        holder.description.setText(article.getDescription());
        holder.date.setText(DATE.get().format(article.getDate()));
    }

    public void setItems(List<Article> news) {
        this.news = news == null ? Collections.emptyList() : new ArrayList<>(news);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    class ArticleHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title) TextView title;
        @BindView(R.id.description) TextView description;
        @BindView(R.id.date) TextView date;

        public ArticleHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
