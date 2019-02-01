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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.FixedHeightViewSizer;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.squareup.picasso.Picasso;

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

  // Model of the corner.  We use completable futures here to simplify
  // the error handling and asynchronous loading.  The loading is started with the
  // first construction of an instance, and then used when the image is set.
  private static CompletableFuture<Void> rendobject;

  private ViewRenderable testViewRenderable;

  public AugmentedImageNode(Context context) {
    // Upon construction, start loading the models for the corners of the frame.
      //if (rendobject == null) {
          SharedPreferences settings = context.getSharedPreferences("MyPref", MODE_PRIVATE);
          int v = settings.getInt("Key", 0);



          if (v == 0) {

              ImageView imageView = new ImageView(context);
              //GlideUrl url = new GlideUrl("http://i.imgur.com/DvpvklR.png");
              Glide.with(context)
                      //.asDrawable()
                      //.asGif()
                      .load(R.drawable.s1)
                      //.load("file:///root/sdcard/Download.Goku.png")
                      //.load(url)
                      //.submit(-1,-1)
                      .into(imageView);

            rendobject =
              ViewRenderable.builder()
                      .setView(context, imageView)
                      .setVerticalAlignment(ViewRenderable.VerticalAlignment.BOTTOM)
                      //.setSizer(new FixedWidthViewSizer(0.2f))
                      .setSizer(new FixedHeightViewSizer(0.2f))
                      .build()
                      .thenAccept(renderable -> {
                                  testViewRenderable = renderable;
                      });

          }

          if (v == 1) {

              ImageView imageView = new ImageView(context);
              //GlideUrl url = new GlideUrl("http://i.imgur.com/DvpvklR.png");
              Glide.with(context)
                      //.asDrawable()
                      //.asGif()
                      .load(R.drawable.s2)
                      //.load("file:///root/sdcard/Download.Goku.png")
                      //.load(url)
                      //.submit(-1,-1)
                      .into(imageView);
                          rendobject =
                                  ViewRenderable.builder()
                                          .setView(context, imageView)
                                          .setVerticalAlignment(ViewRenderable.VerticalAlignment.BOTTOM)
                                          //.setSizer(new FixedWidthViewSizer(0.2f))
                                          .setSizer(new FixedHeightViewSizer(0.2f))
                                          .build()
                                          .thenAccept(renderable -> {
                                                      testViewRenderable = renderable;

                                                });

          }


      //View view = View.inflate(context, R.layout.test_view, null);
      if (v == 2) {

                              ImageView imageView = new ImageView(context);
                              GlideUrl url = new GlideUrl("http://i.imgur.com/DvpvklR.png");
                              Glide.with(context)
                                      //.asDrawable()
                                      //.asGif()
                                      .load(R.drawable.s3)
                                      //.load("file:///root/sdcard/Download.Goku.png")
                                      //.load(url)
                                      //.submit(-1,-1)
                                      .into(imageView);

                       rendobject =
                              ViewRenderable.builder()
                                .setView(context, imageView)
                                .setVerticalAlignment(ViewRenderable.VerticalAlignment.BOTTOM)
                                .setSizer(new FixedHeightViewSizer(0.2f))
                                .build()
                                .thenAccept(renderable -> {
                                    testViewRenderable = renderable;
                                //ImageView imageView = (ImageView) renderable.getView();

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
                     });


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
    Vector3 localPosition = new Vector3();
    Node cornerNode;

    //localPosition.set(-0.5f * image.getExtentX(), 0.0f, -0.5f * image.getExtentZ());
    localPosition.set(-0.0f * image.getExtentX(), 0.1f, +0.2f * image.getExtentZ());
    cornerNode = new Node();
    cornerNode.setParent(this);
    cornerNode.setLocalPosition(localPosition);
    cornerNode.setLocalRotation(Quaternion.axisAngle(new Vector3(-1f, 0, 0), 90f));
    cornerNode.setRenderable(testViewRenderable);
  }
}

