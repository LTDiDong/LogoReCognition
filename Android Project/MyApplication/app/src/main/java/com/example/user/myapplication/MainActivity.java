import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;



import android.os.Environment;
import android.util.Log;

public class ImageMatcher {

    private static final int FEATUREDETECTOR = FeatureDetector.ORB;

    private static final int DESCRIPTOREXTRACTOR = DescriptorExtractor.ORB;

    String mPath;

    List<Mat> lMatDescriptor;
    List<MatOfKeyPoint> lKeypoints;
    List<Mat> lObj_corners;
    List<Mat> lObjects;
    ArrayList<String> lObject;
    String mLastImage="";

    public String getmLastImage() {
        return mLastImage;
    }

    ImageMatcher()
    {
  /*
   *  Constructor. Scans the images to match and pre calculates their
   *  key points and features
   *
   *
   *
   */


        // The path where the images to match are stored

        mPath=Environment.getExternalStorageDirectory()+"/robot/objects/";


        File root = new File(mPath);
        lMatDescriptor=new LinkedList<Mat>();
        lObj_corners=new LinkedList<Mat>();
        lObjects=new LinkedList<Mat>();
        lKeypoints= new LinkedList<MatOfKeyPoint>();
        lObject=new ArrayList<String>();

        FeatureDetector detector = FeatureDetector.create(FEATUREDETECTOR);
        DescriptorExtractor descriptor = DescriptorExtractor.create(DESCRIPTOREXTRACTOR);;


        FilenameFilter pngFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.toLowerCase().endsWith(".png")||name.toLowerCase().endsWith(".jpg"));

            };
        };

        File[] imageFiles = root.listFiles(pngFilter);



        Mat img;
        Mat descriptors1;
        MatOfKeyPoint keypoints1;

        // we calculate the images keypoints, and we store them

        for (File image : imageFiles) {
            String p = image.getAbsolutePath();
            img = Highgui.imread(p);
            Mat imggray = new Mat();
            Imgproc.cvtColor(img,imggray,Imgproc.COLOR_BGR2GRAY);
            lObjects.add(imggray);

            descriptors1= new Mat();
            keypoints1 = new MatOfKeyPoint();

            detector.detect(imggray, keypoints1);
            descriptor.compute(imggray, keypoints1, descriptors1);

            lKeypoints.add(keypoints1);
            lMatDescriptor.add(descriptors1);

            Mat obj_corners = new Mat(4,1,CvType.CV_32FC2);

            obj_corners.put(0, 0, new double[] {0,0});
            obj_corners.put(1, 0, new double[] {img.cols(),0});
            obj_corners.put(2, 0, new double[] {img.cols(),img.rows()});
            obj_corners.put(3, 0, new double[] {0,img.rows()});
            lObj_corners.add(obj_corners);

            int i1=p.lastIndexOf("/")+1;
            int i2=p.lastIndexOf(".");

            String s = p.substring(i1, i2);
            lObject.add(s);
        }


    }

    ///////////////////////////////////////////////////////////////////////////

    Mat Match(Mat img2, boolean draw) {

/*
 *   Mathes img2 against the stores images.
 *   Returns null if there is no match, or an Mat contyaining an image with the
 *   key points matched
 *
 *
 */
        long t1=System.currentTimeMillis();

        FeatureDetector detector = FeatureDetector.create(FEATUREDETECTOR);
        DescriptorExtractor descriptor = DescriptorExtractor
                .create(DESCRIPTOREXTRACTOR);
        ;
        DescriptorMatcher matcher = DescriptorMatcher
                .create(DescriptorMatcher.BRUTEFORCE);
        Double max_dist = 0.0;
        Double min_dist = 100.0;

        MatOfDMatch matches = new MatOfDMatch();
        Mat descriptors2 = new Mat();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();

        detector.detect(img2, keypoints2);
        descriptor.compute(img2, keypoints2, descriptors2);

        int n = -1;
        Iterator<Mat> iterator = lMatDescriptor.iterator();
        Iterator<MatOfKeyPoint> iteratork = lKeypoints.iterator();
        Iterator<Mat> iteratorCorners =  lObj_corners.iterator();
        Iterator<Mat> iteratorObjects =  lObjects.iterator();


        MatOfKeyPoint keypoints1;
        Mat obj_corners;

        while (iterator.hasNext()) {
            Mat img1 = iteratorObjects.next();
            Mat descriptors1 = iterator.next();
            keypoints1= iteratork.next();
            obj_corners= iteratorCorners.next();
            n++;

            if ((descriptors2!=null)&&(descriptors1.type() == descriptors2.type()) && ( descriptors1.cols() == descriptors2.cols()))
            {
                matcher.match(descriptors1, descriptors2, matches);
            }
            else continue;



            List<DMatch> matchesList = matches.toList();
            for (int i = 0; i < descriptors1.rows(); i++) {
                Double dist = (double) matchesList.get(i).distance;
                if (dist < min_dist)
                    min_dist = dist;
                if (dist > max_dist)
                    max_dist = dist;
            }

            System.out.println("-- Max dist : " + max_dist);
            System.out.println("-- Min dist : " + min_dist);

            LinkedList<DMatch> good_matches = new LinkedList<DMatch>();


            for (int i = 0; i < descriptors1.rows(); i++) {
                if (matchesList.get(i).distance < 3 * min_dist) {
                    good_matches.addLast(matchesList.get(i));
                }
            }



            ///// Calcular la homografía y su área

            LinkedList<Point> objList = new LinkedList<Point>();
            LinkedList<Point> sceneList = new LinkedList<Point>();

            List<KeyPoint> keypoints_objectList = keypoints1.toList();
            List<KeyPoint> keypoints_sceneList = keypoints2.toList();



            for(int i = 0; i<good_matches.size(); i++){
                objList.addLast(keypoints_objectList.get(good_matches.get(i).queryIdx).pt);
                sceneList.addLast(keypoints_sceneList.get(good_matches.get(i).trainIdx).pt);
            }

            MatOfPoint2f obj=new MatOfPoint2f();;
            MatOfPoint2f scene =new MatOfPoint2f();
            obj.fromList(objList);
            scene.fromList(sceneList);

            if (good_matches.size()<10)
                continue;
            else
            {
                Mat hg = Calib3d.findHomography(obj,scene,8,10);


                Mat scene_corners = new Mat(4,1,CvType.CV_32FC2);


                Core.perspectiveTransform(obj_corners,scene_corners, hg);

                double d =Imgproc.contourArea(scene_corners);

                Point points[]=new Point[4];
                for (int j=0;j<4;j++)
                {
                    points[j]=new Point(scene_corners.get(j,0));
                    points[j].x+=img1.cols();
                }

                // Test the matchig points. If they are convex, they make sense.

                boolean convex = isConvex(points);



                //////////// draw result.

                if ((d>10)&&(convex))
                    if (convex)
                    {

                        System.out.println("Time "+ (System.currentTimeMillis()-t1));
                        Log.w("Time","Time "+ (System.currentTimeMillis()-t1));

                        mLastImage=lObject.get(n);
                        if (!draw)
                            return img2;

                        Mat outputImg= new Mat();
                        Scalar RED = new Scalar(255,0,0);
                        Scalar GREEN = new Scalar(0,255,0);
                        MatOfByte drawnMatches = new MatOfByte();

                        MatOfDMatch matches_final_mat = new MatOfDMatch();
                        //
                        matches_final_mat.fromList(good_matches);

                        Features2d.drawMatches(img1, keypoints1, img2, keypoints2, matches_final_mat,
                                outputImg, GREEN, RED,  drawnMatches, 0);




//    Core.putText(outputImg, ""+convex, new Point(40,400), 3,6, new Scalar(0, 0,255),2);


                        ////////////////////////////////////////////////

                        Core.line(outputImg, points[0],points[1], new Scalar(0, 0,255),14);
                        Core.line(outputImg, points[1],points[2], new Scalar(0, 0,255),14);
                        Core.line(outputImg,  points[2],points[3], new Scalar(0, 0,255),14);
                        Core.line(outputImg,  points[3],points[0], new Scalar(0, 0,255),14);

                        return outputImg;
                    }

            boolean isConvex(Point[] p)
            {

                Point[] pext=new Point[p.length+2];

                for (int i=0;i<p.length;i++)
                {
                    pext[i]=p[i];
                }
                pext[p.length]=p[0];
                pext[p.length+1]=p[1];

                double total=0;
                for (int k =0;k<p.length;k++)
                {
                    double dx1 = pext[k+1].x-pext[k].x;
                    double dy1 = pext[k+1].y-pext[k].y;
                    double dx2 = pext[k+2].x-pext[k+1].x;
                    double dy2 = pext[k+2].y-pext[k+1].y;
                    double zcrossproduct = dx1*dy2 - dy1*dx2;
                    total += Math.signum(zcrossproduct);
                }
                if ((int)total==p.length)
                    return true;

                return false;
            }


        }