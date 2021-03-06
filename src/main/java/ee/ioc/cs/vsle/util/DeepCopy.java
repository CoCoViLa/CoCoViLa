/**
 * 
 */
package ee.ioc.cs.vsle.util;

/*-
 * #%L
 * CoCoViLa
 * %%
 * Copyright (C) 2003 - 2017 Institute of Cybernetics at Tallinn University of Technology
 * %%
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
 * #L%
 */

import java.io.*;

/**
 * http://javatechniques.com/public/java/docs/basics/faster-deep-copy.html
 * 
 * Utility for making deep copies (vs. clone()'s shallow copies) of 
 * objects. Objects are first serialized and then deserialized. Error
 * checking is fairly minimal in this implementation.
 */
public class DeepCopy {

    public static long time = 0;
    public static long count = 0;
    
    /**
     * Returns a copy of the object, or throws exception if the object cannot
     * be serialized.
     */
    public static <T> T copy( final T orig ) throws Exception {
		count++;
        long _currTime = System.currentTimeMillis();
        
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream( bos );
		oos.writeObject( orig );
		oos.flush();

		ByteArrayInputStream bis = new ByteArrayInputStream( bos.toByteArray() );

		oos.close();

		ObjectInputStream ois = new ObjectInputStream( bis ) {
      @Override
      protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        
        Class<?> result = null;
        
        try {
          result = Class.forName(desc.getName(), false, orig.getClass().getClassLoader());
        } catch(Throwable e) {
          //ignore
        }
        
        if(result == null)
          result = super.resolveClass(desc);
        
        return result;
      }
		};

		// Hide the ugly warning caused by the following unchecked cast
		// as this is unavoidable under current Java type system.
		// Besides, this should never cause problems because we
		// produced the stream we are interpreting from an object
		// of type T.
		@SuppressWarnings("unchecked")
		T copy = (T) ois.readObject();

		ois.close();

		time += (System.currentTimeMillis() - _currTime);
		
		return copy;
		
//		// Write the object out to a byte array
//		FastByteArrayOutputStream fbos = 
//			new FastByteArrayOutputStream();
//		ObjectOutputStream out = new ObjectOutputStream(fbos);
//		out.writeObject(orig);
//		out.flush();
//		out.close();
//
//		// Retrieve an input stream from the byte array and read
//		// a copy of the object back in. 
//		return (T) new ObjectInputStream( fbos.getInputStream() ).readObject();
	}

}

/**
 * ByteArrayOutputStream implementation that doesn't synchronize methods
 * and doesn't copy the data on toByteArray().
 */
class FastByteArrayOutputStream extends OutputStream {
    /**
     * Buffer and size
     */
    protected byte[] buf = null;
    protected int size = 0;

    /**
     * Constructs a stream with buffer capacity size 5K 
     */
    public FastByteArrayOutputStream() {
        this(5 * 1024);
    }

    /**
     * Constructs a stream with the given initial size
     */
    public FastByteArrayOutputStream(int initSize) {
        this.size = 0;
        this.buf = new byte[initSize];
    }

    /**
     * Ensures that we have a large enough buffer for the given size.
     */
    private void verifyBufferSize(int sz) {
        if (sz > buf.length) {
            byte[] old = buf;
            buf = new byte[Math.max(sz, 2 * buf.length )];
            System.arraycopy(old, 0, buf, 0, old.length);
            old = null;
        }
    }

    public int getSize() {
        return size;
    }

    /**
     * Returns the byte array containing the written data. Note that this
     * array will almost always be larger than the amount of data actually
     * written.
     */
    public byte[] getByteArray() {
        return buf;
    }

    @Override
    public final void write(byte b[]) {
        verifyBufferSize(size + b.length);
        System.arraycopy(b, 0, buf, size, b.length);
        size += b.length;
    }

    @Override
    public final void write(byte b[], int off, int len) {
        verifyBufferSize(size + len);
        System.arraycopy(b, off, buf, size, len);
        size += len;
    }

    @Override
    public final void write(int b) {
        verifyBufferSize(size + 1);
        buf[size++] = (byte) b;
    }

    public void reset() {
        size = 0;
    }

    /**
     * Returns a ByteArrayInputStream for reading back the written data
     */
    public InputStream getInputStream() {
        return new FastByteArrayInputStream(buf, size);
    }

}

/** 
 * ByteArrayInputStream implementation that does not synchronize methods.
 */
class FastByteArrayInputStream extends InputStream {
   /** 
    * Our byte buffer
    */
   protected byte[] buf = null;

   /** 
    * Number of bytes that we can read from the buffer
    */
   protected int count = 0;

   /** 
    * Number of bytes that have been read from the buffer
    */
   protected int pos = 0;

   public FastByteArrayInputStream(byte[] buf, int count) {
       this.buf = buf;
       this.count = count;
   }

   @Override
   public final int available() {
       return count - pos;
   }

   @Override
   public final int read() {
       return (pos < count) ? (buf[pos++] & 0xff) : -1;
   }

   @Override
   public final int read(byte[] b, int off, int len) {
       if (pos >= count)
           return -1;

       if ((pos + len) > count)
           len = (count - pos);

       System.arraycopy(buf, pos, b, off, len);
       pos += len;
       return len;
   }

   @Override
   public final long skip(long n) {
       if ((pos + n) > count)
           n = count - pos;
       if (n < 0)
           return 0;
       pos += n;
       return n;
   }

}
