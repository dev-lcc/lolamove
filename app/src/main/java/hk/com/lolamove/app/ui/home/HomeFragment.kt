package hk.com.lolamove.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import hk.com.lolamove.app.LolaMoveBaseFragment
import hk.com.lolamove.app.R
import hk.com.lolamove.app.databinding.FragmentHomeBinding
import hk.com.lolamove.app.ui.home.deliveries.DeliveriesFragment
import hk.com.lolamove.app.ui.home.favorites.FavoritesFragment
import hk.com.lolamove.app.ui.home.myaccount.MyAccountFragment
import timber.log.Timber

class HomeFragment: LolaMoveBaseFragment() {

    private val binding: FragmentHomeBinding by viewBinding(FragmentHomeBinding::bind)

    private var viewPagerAdapter: ViewPager2Adapter? = null
    @Suppress("DEPRECATION")
    private var bottomNavItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_home, container, false)

    override fun onDestroyView() {
        // De-init
        binding.viewpager.adapter = null
        viewPagerAdapter = null

        @Suppress("DEPRECATION")
        binding.bottomNav.setOnNavigationItemReselectedListener(null)
        bottomNavItemSelectedListener = null

        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Setup ViewPager
         */
        // Disable swipe gesture
        binding.viewpager.isUserInputEnabled = false
        binding.viewpager.offscreenPageLimit = 1

        // Setup ViewPager2 with BottomNav Selection
        viewPagerAdapter = ViewPager2Adapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        binding.viewpager.adapter = viewPagerAdapter

        // Set Bottom Nav Selected Listener
        @Suppress("DEPRECATION")
        bottomNavItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                var screenName: String? = null
                var screenClass: String? = null
                when (item.itemId) {
                    R.id.fragmentDeliveries -> {
                        binding.viewpager.setCurrentItem(TAB_DELIVERIES, false)

                        screenName = DeliveriesFragment::class.java.simpleName
                        screenClass = DeliveriesFragment::class.java.`package`!!.name
                    }
                    R.id.fragmentFavorites -> {
                        binding.viewpager.setCurrentItem(TAB_FAVORITES, false)

                        screenName = FavoritesFragment::class.java.simpleName
                        screenClass = FavoritesFragment::class.java.`package`!!.name
                    }
                    R.id.fragmentMyAccount -> {
                        binding.viewpager.setCurrentItem(TAB_MY_ACCOUNT, false)

                        screenName = MyAccountFragment::class.java.simpleName
                        screenClass = MyAccountFragment::class.java.`package`!!.name
                    }
                }

                // Log screen view(bottom nav item selected)
                Timber.d("onViewCreated()::bottomNavItemSelectedListener -> [\"$screenName\" ~> \"$screenClass\"]")
                true
            }
        @Suppress("DEPRECATION")
        binding.bottomNav.setOnNavigationItemSelectedListener(bottomNavItemSelectedListener)

    }

    inner class ViewPager2Adapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun getItemCount(): Int = TAB_COUNT

        override fun createFragment(position: Int): Fragment =
            when(position) {
                TAB_DELIVERIES -> DeliveriesFragment()
                TAB_FAVORITES -> FavoritesFragment()
                TAB_MY_ACCOUNT -> MyAccountFragment()
                else -> Fragment()
            }
    }

    companion object {
        const val TAB_COUNT = 3

        const val TAB_DELIVERIES = 0
        const val TAB_FAVORITES = 1
        const val TAB_MY_ACCOUNT = 2
    }

}