package hk.com.lolamove.app.ui.home.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import hk.com.lolamove.app.LolaMoveBaseFragment
import hk.com.lolamove.app.R
import hk.com.lolamove.app.databinding.FragmentFavoritesBinding

class FavoritesFragment: LolaMoveBaseFragment() {

    private val binding: FragmentFavoritesBinding by viewBinding(FragmentFavoritesBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_favorites, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}