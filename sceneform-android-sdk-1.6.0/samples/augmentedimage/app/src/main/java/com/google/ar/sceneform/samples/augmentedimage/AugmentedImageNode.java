/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ar.sceneform.samples.augmentedimage;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.ColorSpace;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.google.android.filament.Texture;
import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.FixedHeightViewSizer;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.concurrent.CompletableFuture;

import static android.content.Context.MODE_PRIVATE;

/**
 * Node for rendering an augmented image. The image is framed by placing the virtual picture frame
 * at the corners of the augmented image trackable.
 */
@SuppressWarnings({"AndroidApiChecker"})
public class AugmentedImageNode extends AnchorNode {

  private static final String TAG = "AugmentedImageNode";

    // The augmented image represented by this node.
  private AugmentedImage image;
    private ArFragment arFragment;

    @Nullable
    private ModelRenderable videoRenderable;
    private MediaPlayer mediaPlayer;

    // The color to filter out of the video.
    private static final Color CHROMA_KEY_COLOR = new Color(0.1843f, 1.0f, 0.098f);

    // Controls the height of the video in world space.
    private static final float VIDEO_HEIGHT_METERS = 0.20f;


    // Model of the corner.  We use completable futures here to simplify
  // the error handling and asynchronous loading.  The loading is started with the
  // first construction of an instance, and then used when the image is set.
  private static CompletableFuture<Void> rendobject;

  private ViewRenderable testViewRenderable;
  private ViewRenderable getTestViewRenderable2;




  public AugmentedImageNode(Context context) {
    // Upon construction, start loading the models for the corners of the frame.
      //if (rendobject == null) {
          SharedPreferences settings = context.getSharedPreferences("MyPref", MODE_PRIVATE);
          int v = settings.getInt("Key", 0);



          if (v == 1) {



              ImageView imageView = new ImageView(context);
              //Toast.makeText(context, "This is my Toast message!", Toast.LENGTH_LONG).show();

              //String imageUri = "https://i.imgur.com/P4fO2HC.jpg";

              //http://fossilinsects.myspecies.info/sites/fossilinsects.myspecies.info/files/styles/slideshow_large/public/allparticipants.jpg?itok=QSckMPjG
              Picasso.get()
                      .load(R.drawable.resume)
                      .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                      .into(imageView);

            rendobject =
              ViewRenderable.builder()
                      .setView(context, imageView)
                      .setVerticalAlignment(ViewRenderable.VerticalAlignment.BOTTOM)
                      .setSizer(new FixedHeightViewSizer(0.04f))
                      .build()
                      .thenAccept(renderable -> {
                                  getTestViewRenderable2 = renderable;

                          Vector3 localPosition = new Vector3();
                          Node cornerNode2;

                          //localPosition.set(-0.5f * image.getExtentX(), 0.0f, -0.5f * image.getExtentZ());
                          localPosition.set(+0.4f * image.getExtentX(), 0.01f, +0.2f * image.getExtentZ());
                          cornerNode2 = new Node();
                          cornerNode2.setParent(this);
                          cornerNode2.setLocalPosition(localPosition);
                          cornerNode2.setLocalRotation(Quaternion.axisAngle(new Vector3(-1f, 0, 0), 90f));
                          cornerNode2.setRenderable(getTestViewRenderable2);

                      });

          }

          if (v == 0) {

              //ImageView imageView = new ImageView(context);
              //Toast.makeText(context, "This is my Toast message!", Toast.LENGTH_LONG).show();
              //String imageUri = "https://image.slidesharecdn.com/itinerary3days2nights-160124163307/95/nueva-ecija-3days-2nights-itinerary-1-638.jpg";



              //arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

              // Create an ExternalTexture for displaying the contents of the video.
              ExternalTexture texture = new ExternalTexture();



              // Create an Android MediaPlayer to capture the video on the external texture's surface.

              mediaPlayer = MediaPlayer.create(context, R.raw.tr);
              //mediaPlayer = MediaPlayer.create(context, Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4") );

         /*     ColorSpace.Connector connector = ColorSpace.connect(
                      ColorSpace.get(ColorSpace.Named.CIE_LAB),
                      ColorSpace.get(ColorSpace.Named.BT709));
*/
              //mediaPlayer = MediaPlayer.create(context, Uri.parse("http://techslides.com/demos/sample-videos/small.mp4") );
              //MediaPlayer mediaPlayer = new MediaPlayer();
              //mediaPlayer.setDataSource("https://www.youtube.com/watch?v=aUN6RPMIoeo");
              //mediaPlayer.prepare();
              mediaPlayer.setSurface(texture.getSurface());
              mediaPlayer.setLooping(true);

              rendobject =
                      ModelRenderable.builder()
                              .setSource(context, R.raw.chroma_key_video)
                              .build()
                              .thenAccept(renderable -> {
                                  videoRenderable = renderable;
                                  renderable.getMaterial().setExternalTexture("videoTexture", texture);
                                  renderable.getMaterial().setFloat4("keyColor", CHROMA_KEY_COLOR);
                              })
                              .exceptionally(
                                      throwable -> {
                                          Toast toast =
                                                  Toast.makeText(context, "Unable to load video renderable", Toast.LENGTH_LONG);
                                          toast.setGravity(Gravity.CENTER, 0, 0);
                                          toast.show();
                                          return null;
                              });



              Node videoNode = new Node();
              videoNode.setParent(this);

              float videoWidth = mediaPlayer.getVideoWidth();
              float videoHeight = mediaPlayer.getVideoHeight();
              videoNode.setLocalScale(
                      new Vector3(
                              VIDEO_HEIGHT_METERS * (videoWidth / videoHeight)*2/5, VIDEO_HEIGHT_METERS*2/5, 1.0f));


// Start playing the video when the first node is placed.
              if (!mediaPlayer.isPlaying()) {
                  mediaPlayer.start();

                  // Wait to set the renderable until the first frame of the  video becomes available.
                  // This prevents the renderable from briefly appearing as a black quad before the video
                  // plays.
                  texture
                          .getSurfaceTexture()
                          .setOnFrameAvailableListener(
                                  (SurfaceTexture surfaceTexture) -> {
                                      Vector3 localPosition = new Vector3();
                                      localPosition.set(+0.1f * image.getExtentX(), +0.1f, +0.3f * image.getExtentZ());
                                      texture.getSurfaceTexture().setOnFrameAvailableListener(null);
                                      videoNode.setLocalPosition(localPosition);
                                      videoNode.setLocalRotation(Quaternion.axisAngle(new Vector3(-1f, 0, 0), 90f));
                                      videoNode.setRenderable(videoRenderable);

                                  });
              } else {
                  videoNode.setLocalRotation(Quaternion.axisAngle(new Vector3(-1f, 0, 0), 90f));
                  videoNode.setRenderable(videoRenderable);
              }


              /*              GlideUrl url = new GlideUrl("http://i.imgur.com/DvpvklR.png");
              Glide.with(context)
                      //.asDrawable()
                      .asGif()
                      .load(R.drawable.dance)
                      //.load("file:///root/sdcard/Download.Goku.png")
                      //.load(url)
                      //.submit(-1,-1)
                      .into(imageView);
                      */
              //http://i.imgur.com/DvpvklR.png
             /* Picasso.get()
                      .load(imageUri)
                      .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                      .into(imageView);
*/

          }


       if (v == 2) {
           ExternalTexture texture = new ExternalTexture();
           mediaPlayer = MediaPlayer.create(context, R.raw.lion_chroma);
           mediaPlayer.setSurface(texture.getSurface());
           mediaPlayer.setLooping(true);

           rendobject =
                   ModelRenderable.builder()
                           .setSource(context, R.raw.chroma_key_video)
                           .build()
                           .thenAccept(renderable -> {
                               videoRenderable = renderable;
                               renderable.getMaterial().setExternalTexture("videoTexture", texture);
                               renderable.getMaterial().setFloat4("keyColor", CHROMA_KEY_COLOR);
                           })
                           .exceptionally(
                                   throwable -> {
                                       Toast toast =
                                               Toast.makeText(context, "Unable to load video renderable", Toast.LENGTH_LONG);
                                       toast.setGravity(Gravity.CENTER, 0, 0);
                                       toast.show();
                                       return null;
                                   });


           Node videoNode = new Node();
           videoNode.setParent(this);

           float videoWidth = mediaPlayer.getVideoWidth();
           float videoHeight = mediaPlayer.getVideoHeight();
           videoNode.setLocalScale(
                   new Vector3(
                           VIDEO_HEIGHT_METERS * (videoWidth / videoHeight)*2/3, VIDEO_HEIGHT_METERS*2/3, 1.0f));
           if (!mediaPlayer.isPlaying()) {
               mediaPlayer.start();
               texture
                       .getSurfaceTexture()
                       .setOnFrameAvailableListener(
                               (SurfaceTexture surfaceTexture) -> {
                                   Vector3 localPosition = new Vector3();
                                   localPosition.set(+0.1f * image.getExtentX(), +0.2f, +0.3f * image.getExtentZ());
                                   texture.getSurfaceTexture().setOnFrameAvailableListener(null);
                                   videoNode.setLocalPosition(localPosition);
                                   videoNode.setLocalRotation(Quaternion.axisAngle(new Vector3(-1f, 0, 0), 90f));
                                   videoNode.setRenderable(videoRenderable);

                               });
           } else {
               videoNode.setLocalRotation(Quaternion.axisAngle(new Vector3(-1f, 0, 0), 90f));
               videoNode.setRenderable(videoRenderable);
           }

        /*                     ImageView imageView = new ImageView(context);

                               //String imageUri = "https://cdn-images-1.medium.com/max/1200/1*d6Gb-e7bNDo0RUL-f5jgmw.png";
                               //http://fossilinsects.myspecies.info/sites/fossilinsects.myspecies.info/files/styles/slideshow_large/public/allparticipants.jpg?itok=QSckMPjG

                               Picasso.get()
                                       .load(R.drawable.grayicons2)
                                       .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                       .into(imageView);




                       rendobject =
                              ViewRenderable.builder()
                                .setView(context, imageView)
                                .setVerticalAlignment(ViewRenderable.VerticalAlignment.BOTTOM)
                                .setSizer(new FixedHeightViewSizer(0.20f))
                                .build()
                                .thenAccept(renderable -> {
                                    testViewRenderable = renderable;


*/

                      /*   Picasso.get()
                                 .load(R.drawable.ai)
                                 .fit()
                                 .centerInside()
                                 .into(imageView);

                                 testViewRenderable = renderable;
                                //.load(Environment.getExternalStorageDirectory().getPath()+"Download/Goku.png")
                                //.load("file:///root/sdcard/Download.Goku.png")
                                //.load("http://i.imgur.com/DvpvklR.png")
*/
           // });


  }

 /*      ModelRenderable.builder()
                      .setSource(context, Uri.parse("models/tinker.sfb"))
                      .build();
                */
    }
  //}


    /**
   * Called when the AugmentedImage is detected and should be rendered. A Sceneform node tree is
   * created based on an Anchor created from the image. The cornerNode is then positioned based on the
   * extent of the image. There is no need to worry about world coordinates since everything is
   * relative to the center of the image, which is the parent node of the corner.
   */
  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  public void setImage(AugmentedImage image) {
    this.image = image;

    // If any of the models are not loaded, then recurse when all are loaded.
    if (!rendobject.isDone())
      {
      CompletableFuture.allOf(rendobject)//, urCorner, llCorner, lrCorner)
          .thenAccept((Void aVoid) -> setImage(image))
          .exceptionally(
              throwable -> {
                Log.e(TAG, "Exception loading", throwable);
                return null;
              });
    }

    // Set the anchor based on the center of the image.
    setAnchor(image.createAnchor(image.getCenterPose()));

    // Make the node(s).
 /*
    Vector3 localPosition = new Vector3();
    Node cornerNode;

    //localPosition.set(-0.5f * image.getExtentX(), 0.0f, -0.5f * image.getExtentZ());
    localPosition.set(-0.0f * image.getExtentX(), 0.30f, +0.5f * image.getExtentZ());
    cornerNode = new Node();
    cornerNode.setParent(this);
    cornerNode.setLocalPosition(localPosition);
    cornerNode.setLocalRotation(Quaternion.axisAngle(new Vector3(-1f, 0, 0), 90f));
    cornerNode.setRenderable(testViewRenderable);
    //cornerNode.setRenderable(videoRenderable);
  */
  }
}

