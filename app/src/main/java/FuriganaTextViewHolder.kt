import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.FuriganaTextView
import com.example.myapplication.R

class FuriganaTextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val furiganaTextView: FuriganaTextView = view.findViewById(R.id.furiganaTextView)
}
