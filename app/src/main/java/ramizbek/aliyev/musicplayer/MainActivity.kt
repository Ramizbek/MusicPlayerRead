package ramizbek.aliyev.musicplayer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import ramizbek.aliyev.musicplayer.Adapters.MyAdapter
import ramizbek.aliyev.musicplayer.Classes.MyMusic
import ramizbek.aliyev.musicplayer.Object.MyObject
import ramizbek.aliyev.musicplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var myListAdapter: MyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.apply {
            setContentView(binding.root)

            sayPermission()

            myListAdapter = MyAdapter(musicFiles(), object : MyAdapter.MyOnClick {
                override fun onClick(myMusic: MyMusic, position: Int) {
                    MyObject.list = musicFiles()
                    MyObject.myMusic = myMusic
                    MyObject.position = position
                    MyObject.play = true
                    startActivity(Intent(root.context, MainActivity2::class.java))
                }
            })
            rv.adapter = myListAdapter

        }
    }

    @SuppressLint("Range")
    private fun musicFiles(): ArrayList<MyMusic> {
        val list = ArrayList<MyMusic>()
        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            MediaStore.Audio.Media.IS_MUSIC + "!= 0",
            null,
            MediaStore.Audio.Media.TITLE + " ASC"
        )
        if (cursor != null && cursor.moveToFirst()) {
            do {
                cursor.apply {
                    val title = getString(getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val path = getString(getColumnIndex(MediaStore.Audio.Media.DATA))
                    val artist = getString(getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    list.add(MyMusic(path, title, artist))
                }
            } while (cursor.moveToNext())
        }
        return list
    }

    private fun sayPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            val builder = AlertDialog.Builder(binding.root.context).apply {
                setTitle("Permission")
                setMessage("Allow the current license to play music on your phone")
                setCancelable(false)
                setIcon(R.drawable.pause_music)
                setPositiveButton("Ok") { _, _ ->
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
                    )
                }
            }
            builder.create().show()
        } else {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
            )
        }
    }
}