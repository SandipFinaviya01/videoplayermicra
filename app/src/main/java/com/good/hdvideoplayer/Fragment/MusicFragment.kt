package com.good.hdvideoplayer.Fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.good.hdvideoplayer.R
import com.good.hdvideoplayer.adapter.MusicPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MusicFragment : Fragment() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Enable menu for fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_music, container, false)

        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabLayout)

        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarMusic)
        toolbar.inflateMenu(R.menu.menu_music_toolbar)

        // ✅ Setup SearchView separately
        val searchMenuItem = toolbar.menu.findItem(R.id.menu_search)
        val searchView = searchMenuItem.actionView as androidx.appcompat.widget.SearchView
        searchView.queryHint = "Search Song..."

        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val songsFragment = childFragmentManager.fragments
                    .firstOrNull { it is SongsFragment } as? SongsFragment
                songsFragment?.filterSongs(newText ?: "")
                return true
            }
        })

        // ✅ Now only handle item clicks
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_select -> {
                    Toast.makeText(requireContext(), "Select clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_network_stream -> {
                    Toast.makeText(requireContext(), "Network Stream clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_theme -> {
                    Toast.makeText(requireContext(), "Theme clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_widgets -> {
                    Toast.makeText(requireContext(), "Widgets clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_refresh -> {
                    Toast.makeText(requireContext(), "Refresh clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_equalizer -> {

                   true
                }
                R.id.menu_remove_ads -> {
                    Toast.makeText(requireContext(), "Remove Ads clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_settings -> {
                    Toast.makeText(requireContext(), "Settings clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_search -> {
                    true
                }
                else -> false
            }
        }

        // Tabs setup
        val fragments = listOf(
            SongsFragment(),
            FoldersFragment(),
            AlbumsFragment(),
            ArtistsFragment()
        )
        val titles = listOf("SONGS", "FOLDERS", "ALBUMS", "ARTISTS")

        viewPager.adapter = MusicPagerAdapter(this, fragments)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()

        return view
    }



    override fun onDestroyView() {
        super.onDestroyView()
        // Restore Activity Toolbar if needed
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.top_menu, menu)
//        val searchItem = menu.findItem(R.id.menu_search)
//        val searchView = searchItem.actionView as SearchView
//
//        val songsFragment = childFragmentManager.findFragmentByTag("f0") as? SongsFragment
//            ?: (childFragmentManager.fragments.firstOrNull{it is SongsFragment } as? SongsFragment)
//
//        searchView.queryHint = "Search Song..."
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                songsFragment?.filterSongs(newText ?: "")
//                return true
//            }
//        })
//
//        super.onCreateOptionsMenu(menu, inflater)
//    }

}

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.action_search -> {
//                Toast.makeText(requireContext(), "Search clicked", Toast.LENGTH_SHORT).show()
//                return true
//            }
//            R.id.action_more -> {
//                Toast.makeText(requireContext(), "More clicked", Toast.LENGTH_SHORT).show()
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

