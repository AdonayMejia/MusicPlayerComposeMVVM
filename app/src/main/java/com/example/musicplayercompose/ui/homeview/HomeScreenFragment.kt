package com.example.musicplayercompose.ui.homeview

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.compose.rememberImagePainter
import com.example.musicplayercompose.R
import com.example.musicplayercompose.model.Song
import com.example.musicplayercompose.model.SongRepository
import com.example.musicplayercompose.model.SongRepository.songs
import com.example.musicplayercompose.model.media.MediaPlayerHolder
import com.example.musicplayercompose.ui.homeview.viewmodel.HomeScreenViewModel
import com.example.musicplayercompose.ui.homeview.viewmodel.HomeScreenViewModelFactory
import com.example.musicplayercompose.ui.playerview.PlayScreenFragment


class HomeScreenFragment : Fragment() {
    private lateinit var viewModel: HomeScreenViewModel
    private var currentSongIndex: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(
            this,
            HomeScreenViewModelFactory(requireContext())
        )[HomeScreenViewModel::class.java]
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val songs by viewModel.uiState.songsStateFlow.collectAsState()
                Scaffold(
                    bottomBar = { BottomBarActions(onSettingsClick = { navigateToSettings() }) }
                ) { paddingValues ->
                    SongList(songs, paddingValues) { song ->
                        onSongClick(song)
                    }
                }
            }

        }
    }

    private fun navigateToSettings() {
        findNavController().navigate(R.id.action_homeScreenFragment_to_settingScreenFragment)
    }

    private fun onSongClick(song: Song) {
        val position = viewModel.uiState.songsStateFlow.value.indexOf(song)
        playSelectedSong(position)

        navigateToDetailActivity(position)
    }

    private fun navigateToDetailActivity(position: Int) {
        val bundle = Bundle().apply {
            putString(PlayScreenFragment.SONG_TITLE_KEY, SongRepository.songs[position].title)
        }
        findNavController().navigate(R.id.action_homeScreenFragment_to_playScreenFragment, bundle)
    }

    private fun playSelectedSong(position: Int) {
        MediaPlayerHolder.mediaPlayer?.release()
        currentSongIndex = position
        MediaPlayerHolder.mediaPlayer = MediaPlayer.create(
            requireContext(),
            viewModel.uiState.songsStateFlow.value[position].songUri
        )
        MediaPlayerHolder.mediaPlayer?.start()
    }

}

@Composable
fun SongList(songs: List<Song>, paddingValues: PaddingValues, onSongClick: (Song) -> Unit) {
    LazyColumn(
        modifier = Modifier.padding(paddingValues),
        contentPadding = PaddingValues(
            horizontal = 0.dp,
            vertical = 8.dp
        ) // Adjust the padding values as needed
    ) {
        items(songs) { song ->
            SongListItem(song, onSongClick)
            Divider()
        }
    }
}


@Composable
fun BottomBarActions(onSettingsClick: () -> Unit) {
    BottomAppBar {
        IconButton(
            onClick = onSettingsClick

        ) {
            Icon(
                Icons.Filled.Settings,
                contentDescription = stringResource(R.string.settings)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = {
                // Handle random song button click
            }
        ) {
            Icon(
                Icons.Filled.Shuffle,
                contentDescription = stringResource(R.string.random_song)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = {
                // Handle play first song button click
            }
        ) {
            Icon(
                Icons.Filled.PlayArrow,
                contentDescription = stringResource(R.string.play_first_song)
            )
        }
    }
}

@Composable
fun SongListItem(song: Song, onClick: (Song) -> Unit) {
    Row(
        modifier = Modifier
            .clickable { onClick(song) }
            .padding(8.dp)
    ) {
        Image(
            painter = rememberImagePainter(data = song.albumArtUri),
            contentDescription = stringResource(R.string.album_art),
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(song.title)
    }
}
