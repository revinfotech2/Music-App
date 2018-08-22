package com.zilla


import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.github.nitrico.lastadapter.Holder
import com.github.nitrico.lastadapter.ItemType
import com.github.nitrico.lastadapter.LastAdapter
import com.zilla.databinding.ActivityMainBinding
import com.zilla.databinding.ItemMainBinding
import com.zilla.network.SearchHandler
import javax.inject.Inject

class MainActivity : BaseActivity(), SearchView.OnQueryTextListener {

    @Inject lateinit var chartsFetcher: ChartsFetcher
    private lateinit var binding: ActivityMainBinding
    private lateinit var searchDebounceHandler: Handler
    private lateinit var searchDebounceRunnable: Runnable
    private var oldSearchText = ""
    private var searchText = ""
    private var searchView: SearchView? = null
    private val Tag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as Application).component.inject(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.recycler.layoutManager = LinearLayoutManager(this)

        chartsFetcher.fetchAppleAlbumFeed { f -> showAlbums(f) }

        searchDebounceHandler = Handler()
        searchDebounceRunnable = Runnable {
            if (searchText != oldSearchText) {
                if (!searchText.isEmpty()) {
                    doSearchText(searchText)
                }
                else {
                    chartsFetcher.fetchAppleAlbumFeed { f -> showAlbums(f) }
                }

                oldSearchText = searchText
            }
            searchDebounceHandler.postDelayed(searchDebounceRunnable, 500)
        }
        searchDebounceHandler.postDelayed(searchDebounceRunnable, 500)
    }


    private fun showAlbums(albums: List<Album>) {
        val itemBinding = object : ItemType<ItemMainBinding>(R.layout.item_main) {
            override fun onBind(holder: Holder<ItemMainBinding>) {

            }
        }
        LastAdapter(albums, BR.album)
                .map<Album>(itemBinding)
                .into(binding.recycler)
        binding.recycler.visibility = View.VISIBLE
        binding.progress.visibility = View.GONE

    }

    private fun doSearchText(text: String) {
        SearchHandler(this).search(searchText, { a ->
            showAlbums(a)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.activity_main, menu)
        val searchItem = menu!!.findItem(R.id.search)
        searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchView!!.queryHint = getString(R.string.search_title)
        searchView!!.setOnQueryTextListener(this)
        MenuItemCompat.setOnActionExpandListener(searchItem, object : MenuItemCompat.OnActionExpandListener {
            override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                ChartsFetcher(this@MainActivity).fetchAppleAlbumFeed { f -> showAlbums(f) }
                return true
            }
        })
        return true
    }


    override fun onQueryTextSubmit(query: String): Boolean {
        if(!query.isEmpty()) {
            doSearchText(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        this.searchText = newText
        return true
    }

}
