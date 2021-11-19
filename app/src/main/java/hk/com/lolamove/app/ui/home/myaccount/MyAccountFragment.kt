package hk.com.lolamove.app.ui.home.myaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import hk.com.lolamove.app.LolaMoveBaseFragment
import hk.com.lolamove.app.R
import hk.com.lolamove.app.databinding.FragmentMyAccountBinding

class MyAccountFragment: LolaMoveBaseFragment() {

    private val binding: FragmentMyAccountBinding by viewBinding(FragmentMyAccountBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_my_account, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}