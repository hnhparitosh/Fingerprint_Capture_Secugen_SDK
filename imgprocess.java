import javax.swing.*;
import javax.imageio.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import SecuGen.FDxSDKPro.jni.*;

class IMGProcess extends JFrame
{
 JSGFPLib o;
 private BufferedImage localBufferedImage;
 private byte[] arrayOfByte;
 private JLabel imgShow;
 private JTextField devName;
 private JButton init,capture,savebmp;
 private JPanel imgContainer;
 public IMGProcess()
 {
  initComponents();
  setLayout(null);
  setTitle("Secugen Image Processing");
  setSize(550,400);
  setVisible(true);
 }
 private void initComponents()
 {
  imgShow = new JLabel();
  imgShow.setBorder(BorderFactory.createLineBorder(Color.red));
  imgShow.setBounds(50,50,350,260);
 
  devName= new JTextField("Secugen Hamster Pro 20");
  devName.setBounds(10,20,150,20);
  devName.setEnabled(false);

  init=new JButton("Initialize");
  init.setBounds(200,20,100,20);
  init.addActionListener(new ActionListener(){  
    public void actionPerformed(ActionEvent e){  
            initialize();  
    }  
    });  

  capture= new JButton("Capture");
  capture.setBounds(340,20,100,20);
  capture.setEnabled(false);
  capture.addActionListener(new ActionListener(){  
    public void actionPerformed(ActionEvent e){  
            captureImage();  
    }  
    });  

  savebmp= new JButton("Save Image As BMP");
  savebmp.setBounds(150,320,150,20);
  savebmp.setEnabled(false);
  savebmp.addActionListener(new ActionListener(){  
    public void actionPerformed(ActionEvent e){  
            saveImage();  
    }  
   });

  /*imgContainer =new JPanel();
  imgContainer.setLayout(null);
  imgContainer.setBorder(BorderFactory.createLineBorder(Color.black));
  imgContainer.add(imgShow);
  imgContainer.setBounds(50,50,350,260);*/

  add(devName);
  add(init);
  add(capture);
  add(savebmp);
  //getContentPane().add(imgContainer);
  add(imgShow);
 }
 private void initialize()
 {
  //JSGFPLib o =new JSGFPLib((UsbManager)getSystemService(Context.USB_SERVICE));
  o =new JSGFPLib();
  long device = o.Init(SGFDxDeviceName.SG_DEV_FDU05);
  long opesuccess = o.OpenDevice(SGPPPortAddr.USB_AUTO_DETECT);
  if(opesuccess==SGFDxErrorCode.SGFDX_ERROR_NONE)
  {
   JOptionPane.showMessageDialog(null,"Device Initialized successfully");
   capture.setEnabled(true);
  }
 }
 private void captureImage()
 {
   long error=0; 
   int img_w=0,img_h=0;
   //byte[] buffer=new byte[img_w*img_h];
   //JOptionPane.showMessageDialog(null,"1");
   SGDeviceInfoParam deviceInfo = new SGDeviceInfoParam();
   //JOptionPane.showMessageDialog(null,"2");
   long e = o.GetDeviceInfo(deviceInfo);
   //JOptionPane.showMessageDialog(null,"3");
   if(e==SGFDxErrorCode.SGFDX_ERROR_NONE)
   {
    //JOptionPane.showMessageDialog(null,"4");
    img_w=deviceInfo.imageWidth;
    img_h=deviceInfo.imageHeight;
   }
   localBufferedImage = new BufferedImage(img_w,img_h,10);
   arrayOfByte = ((DataBufferByte)localBufferedImage.getRaster().getDataBuffer()).getData();
   //////// Getting Image
   try
   {
    error=o.GetImage(arrayOfByte);
   }
   catch(Exception er)
   {
    er.printStackTrace();
   }
   if(error==SGFDxErrorCode.SGFDX_ERROR_NONE)
   {
    imgShow.setIcon(new ImageIcon(localBufferedImage));
    //JOptionPane.showMessageDialog(null,"5");
    JOptionPane.showMessageDialog(null,"Image captured successfully");
    savebmp.setEnabled(true);
   }
 }
 private void saveImage()
 {
  boolean test=false,done=false;
  String path="C:/Users/HnH/Videos/java application/bitmap/";
  File output;
  String s;
  /*do
  {
   s = (String)JOptionPane.showInputDialog(this,"Enter File name");
   if(s!=null && s.length()>0)
   {
    test=true;
   }
  }while(test==false);*/
  
  try
  {
   RenderedImage rImg =localBufferedImage;
   done = ImageIO.write(rImg,"bmp",new File(path+"harry.bmp"));
  }
  catch(Exception er1)
  {
   er1.printStackTrace();
  }
  if(done == true)
  {
   imgShow.setIcon(null);
   savebmp.setEnabled(false);
   JOptionPane.showMessageDialog(null,"File saved successfully.");
  }
 }
 public static void main(String[] args)
 {
  new IMGProcess();
 }
}