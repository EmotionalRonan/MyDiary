/*
 * This file provided by Facebook is for non-commercial testing and evaluation
 * purposes only.  Facebook reserves all rights not expressly granted.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * FACEBOOK BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.kiminonawa.mydiary.entries.photo;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.kiminonawa.mydiary.shared.FileManager.DIARY_ROOT_DIR;

/**
 * Simple drawee recycler view fragment that displays a grid of images.
 */
public class PhotoOverviewFragment extends Fragment {
    /**
     * The topic info
     */
    private long topicId, diaryId;
    private ArrayList<Uri> diaryPhotoFileList;

    /**
     * The bind UI
     */
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;


    public static PhotoOverviewFragment newInstance(long topicId, long diaryId) {
        Bundle args = new Bundle();
        PhotoOverviewFragment fragment = new PhotoOverviewFragment();
        args.putLong("topicId", topicId);
        args.putLong("diaryId", diaryId);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary_photo_overview, container, false);
        unbinder = ButterKnife.bind(this, view);
        topicId = getArguments().getLong("topicId", -1);
        diaryId = getArguments().getLong("diaryId", -1);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FileManager diaryRoot = new FileManager(getActivity(), DIARY_ROOT_DIR);
        File topicRootFile;
        if (diaryId != -1) {
            topicRootFile = new File(diaryRoot.getDirAbsolutePath() + "/" + topicId + "/" + diaryId);
        } else {
            topicRootFile = new File(diaryRoot.getDirAbsolutePath() + "/" + topicId);
        }
        //Load all file form topic dir
        diaryPhotoFileList = new ArrayList<>();
        for (File photoFile : getFilesList(topicRootFile)) {
            diaryPhotoFileList.add(Uri.fromFile(photoFile));
        }
        initRecyclerView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private List<File> getFilesList(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getFilesList(file));
            } else {
                inFiles.add(file);
            }
        }
        return inFiles;
    }

    private void initRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),3);
        recyclerView.setLayoutManager(layoutManager);
        PhotoOverviewAdapter photoOverviewAdapter = new PhotoOverviewAdapter(getActivity(), diaryPhotoFileList);
        recyclerView.setAdapter(photoOverviewAdapter);
        photoOverviewAdapter.setOnItemClickListener(new PhotoOverviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                PhotoDetailViewerDialogFragment photoDetailViewerDialogFragment =
                        PhotoDetailViewerDialogFragment.newInstance(diaryPhotoFileList, position);
                photoDetailViewerDialogFragment.show(getActivity().getSupportFragmentManager(), "diaryPhotoBottomSheet");
            }
        });
        recyclerView.setHasFixedSize(false);
    }

}