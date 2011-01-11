package reactor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.SelectableChannel;
import java.nio.channels.WritableByteChannel;


public class SystemInPipe
{
   Pipe pipe;
   CopyThread copyThread;

   /**
    * 
    * @param in - InputStream
    * @throws IOException if IO is not OK
    */
   public SystemInPipe (InputStream in)
      throws IOException
   {
      pipe = Pipe.open();

      copyThread = new CopyThread (in, pipe.sink());
   }

   /**
    * 
    * @throws IOException IO..
    * 
    */
   public SystemInPipe()
      throws IOException
   {
      this (System.in);
   }

   /**
    * start the pipe.
    */
   public void start()
   {
      copyThread.start();
   }

   /**
    * 
    * @return {@link SelectableChannel}
    * @throws IOException in case of IO trouble
    */
   public SelectableChannel getStdinChannel()
      throws IOException
   {
      SelectableChannel channel = pipe.source();

      channel.configureBlocking (false);

      return (channel);
   }

   protected void finalize()
   {
      copyThread.shutdown();
   }

   // ---------------------------------------------------

   public static class CopyThread extends Thread
   {
      private static final int _128 = 128;
	boolean keepRunning = true;
      byte [] bytes = new byte [_128];
      ByteBuffer buffer = ByteBuffer.wrap (bytes);
      InputStream in;
      WritableByteChannel out;

      CopyThread (InputStream _in, WritableByteChannel _out)
      {
         this.in = _in;
         this.out = _out;
         this.setDaemon (true);
      }

      /**
       * shutdown the pipe
       */
      public void shutdown()
      {
         keepRunning = false;
         this.interrupt();
      }

      /**
       * run method
       */
      public void run()
      {

         try {
            while (keepRunning) {
               int count = in.read (bytes);

               if (count < 0) {
                  break;
               }

               buffer.clear().limit (count);

               out.write (buffer);
            }

            out.close();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
}