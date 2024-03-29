package hk.com.lolamove.app

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.cancelChildren
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ViewModelParameter
import org.koin.androidx.viewmodel.koin.getViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

open class LolaMoveBaseFragment: Fragment() {

    // Top-level Nav Controller Instance
    protected val appNavController: NavController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.onBackPressedDispatcher
            ?.addCallback(
                owner = this@LolaMoveBaseFragment,
                enabled = enableBackPressedCallback(),
                onBackPressed = onBackPressedCallback(),
            )
    }

    /**
     * Helper functions to handle on back pressed callback
     * - Override this and set to `true` if you want the implementing Fragment to enable onBackPressedCallback implementation
     */
    open fun enableBackPressedCallback(): Boolean = false

    /**
     * Override this and set to `true` if you want the implementing Fragment to have its own onBackPressedCallback implementation
     */
    open fun onBackPressedCallback(): OnBackPressedCallback.() -> Unit = {}

    override fun onDestroyView() {
        // Cancel Coroutine Flow Subscriptions launched on `lifecycleScope`
        lifecycleScope.coroutineContext.cancelChildren()

        super.onDestroyView()
    }

    /**
     * Log TAG
     */
    protected val TAG = this::class.java.simpleName

}

/**
 * Helper function to conveniently retrieve ViewModel scoped by navigation graph(ex. nested nav graph)
 * https://github.com/InsertKoinIO/koin/issues/442#issuecomment-623058728
 */
inline fun <reified VM : ViewModel> Fragment.sharedGraphViewModel(
    @IdRes navGraphId: Int,
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) = lazy {
    val owner = findNavController().getViewModelStoreOwner(navGraphId)
    getKoin().getViewModel(
        ViewModelParameter(
            clazz = VM::class,
            viewModelStore = owner.viewModelStore,
            qualifier = qualifier,
            parameters = parameters
        )
    )
}