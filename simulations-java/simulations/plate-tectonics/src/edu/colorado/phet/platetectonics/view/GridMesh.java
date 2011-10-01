// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

/**
 * Displays a grid made of triangles
 */
public class GridMesh extends Mesh {

    // TODO: consider changing positions to be a float buffer? or vertex buffer?
    public GridMesh( int rows, int columns, Vector3f[] positions ) {
        int vertexCount = columns * rows;
        FloatBuffer positionBuffer = BufferUtils.createFloatBuffer( vertexCount * 3 );
        FloatBuffer normalBuffer = BufferUtils.createFloatBuffer( vertexCount * 3 );
        FloatBuffer textureBuffer = BufferUtils.createFloatBuffer( vertexCount * 2 );

        // we build it from strips. there are (rows-1) strips, each strip uses two full rows (columns*2),
        // and for the connections between strips (rows-2 of those), we need 2 extra indices
        int numIndices = ( rows - 1 ) * columns * 2 + ( rows - 2 ) * 2;
        IntBuffer indexBuffer = BufferUtils.createIntBuffer( numIndices );

        float maxSize = Math.max( rows, columns );
        for ( int row = 0; row < rows; row++ ) {
            int rowOffset = row * columns;
            for ( int col = 0; col < columns; col++ ) {
                /*---------------------------------------------------------------------------*
                * position
                *----------------------------------------------------------------------------*/
                Vector3f position = positions[rowOffset + col];
                positionBuffer.put( new float[] { position.x, position.y, position.z } );


                /*---------------------------------------------------------------------------*
                * normal
                *----------------------------------------------------------------------------*/
                Vector3f up;
                Vector3f down;
                Vector3f left;
                Vector3f right;

                // calculate up/down vectors
                if ( row > 0 ) {
                    up = positions[( row - 1 ) * columns + col].subtract( position );
                    down = ( row < rows - 1 ) ? positions[( row + 1 ) * columns + col].subtract( position ) : up.negate();
                }
                else {
                    down = positions[( row + 1 ) * columns + col].subtract( position );
                    up = down.negate();
                }

                // calculate left/right vectors
                if ( col > 0 ) {
                    left = positions[rowOffset + col - 1].subtract( position );
                    right = ( col < columns - 1 ) ? positions[rowOffset + col + 1].subtract( position ) : left.negate();
                }
                else {
                    right = positions[rowOffset + col + 1].subtract( position );
                    left = right.negate();
                }

//                System.out.println( "up = " + up );
//                System.out.println( "down = " + down );
//                System.out.println( "left = " + left );
//                System.out.println( "right = " + right );

                Vector3f normal = new Vector3f();
                // basically, sum up the normals of each quad this vertex is part of, and take the average
                normal.addLocal( right.cross( up ).normalizeLocal() );
                normal.addLocal( up.cross( left ).normalizeLocal() );
                normal.addLocal( left.cross( down ).normalizeLocal() );
                normal.addLocal( down.cross( right ).normalizeLocal() );
                normal.normalizeLocal();
//                System.out.println( "normal = " + normal );
                normalBuffer.put( new float[] { normal.x, normal.y, normal.z } );

                /*---------------------------------------------------------------------------*
                * texture
                *----------------------------------------------------------------------------*/

                // add texture coordinates based on the largest overall space that will fit in our unit square with the correct aspect ratio
                // TODO: better way of handling this? can we scale the terrain textures to make this work better?
                textureBuffer.put( new float[] {
                        ( (float) ( col ) ) / maxSize,
                        ( (float) ( row ) ) / maxSize, // consider moving this out of the loop for optimization
                } );
            }
        }

        // create the index information so OpenGL knows how to walk our position indices
        for ( int strip = 0; strip < rows - 1; strip++ ) {
            int stripOffset = strip * columns;

            if ( strip != 0 ) {
                // add in two points that create volume-less triangles (won't render) and keep the winding number the same for the start
                indexBuffer.put( stripOffset + columns - 1 ); // add the last-added point
                indexBuffer.put( stripOffset );
            }

            // each quad is walked over by hitting (in order) upper-left, lower-left, upper-right, lower-right.
            for ( int offset = 0; offset < columns; offset++ ) {
                indexBuffer.put( stripOffset + offset );
                indexBuffer.put( stripOffset + columns + offset ); // down a row
            }
        }

        setMode( Mode.TriangleStrip );
        setBuffer( VertexBuffer.Type.Position, 3, positionBuffer );
        setBuffer( VertexBuffer.Type.Normal, 3, normalBuffer );
        setBuffer( VertexBuffer.Type.TexCoord, 2, textureBuffer );
        setBuffer( VertexBuffer.Type.Index, 3, indexBuffer );
        updateBound();
        updateCounts();
    }
}
