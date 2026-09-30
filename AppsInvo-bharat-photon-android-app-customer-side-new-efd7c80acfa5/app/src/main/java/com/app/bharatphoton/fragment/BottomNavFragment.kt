package com.app.bharatphoton.fragment


/*
class BottomNavFragment : BaseFragment() {
    private val binding by lazy { FragmentBottomNavBinding.inflate(layoutInflater) }
    private val navigationMap by lazy { mapOf(R.id.homeScreen to 0, R.id.categoriesScreen to 1, R.id.myOrdersScreen to 2, R.id.profileScreen to 3) }
    private val handleKeyBoard = HandleKeyBoard()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        handleKeyBoard.stopKeyboardListener()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleKeyBoard.startKeyboardListener(binding.root) { isKeyboardOpen ->
            binding.BottomNav.LayoutMain.visibility = if (isKeyboardOpen) View.GONE else View.VISIBLE
        }

        setUpNavigationComponent()
    }


    private fun NavController.navUpdateSelectedTabUI(selectedIndex: Int) {
        binding.BottomNav.LayoutMain.forEachIndexed { index, view ->
            val child = view as LinearLayout
            val imageView = child.getChildAt(0) as ImageView
            val separator = child.getChildAt(1) as View
            val textView = child.getChildAt(2) as TextView

            val isSelected = index == selectedIndex

            child.apply {
                setBackgroundResource(if (isSelected) R.drawable._style_bottom_nav_tab else 0)
                isEnabled = !isSelected
                setOnClickListener {
                    navigationMap.entries.find { map -> map.value == index }?.key?.let { destinationId -> navigate(destinationId) }
                }
            }

            imageView.isEnabled = !isSelected
            separator.visibility = if (isSelected) View.VISIBLE else View.GONE
            textView.visibility = if (isSelected) View.VISIBLE else View.GONE

        }
    }

    private fun setUpNavigationComponent() {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.child_nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        navController.apply {
            setGraph(R.navigation.bottom_nav_graph)
            addOnDestinationChangedListener { _, destination, _ ->
                navigationMap[destination.id]?.let { index -> navUpdateSelectedTabUI(index) }
            }
        }
    }

}*/
