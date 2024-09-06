package net.povstalec.sgjourney.common.misc;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3d;

public class Matrix3d
{
    public double m00, m01, m02;
    public double m10, m11, m12;
    public double m20, m21, m22;
	
	public Matrix3d()
	{
		m00 = 1; m01 = 0; m02 = 0;
		m10 = 0; m11 = 1; m12 = 0;
		m20 = 0; m21 = 0; m22 = 1;
	}
	
	public Matrix3d rotate(Quaternion quaternion) 
	{
        double w2 = quaternion.r() * quaternion.r(), x2 = quaternion.i() * quaternion.i();
        double y2 = quaternion.j() * quaternion.j(), z2 = quaternion.k() * quaternion.k();
        double zw = quaternion.k() * quaternion.r(), dzw = zw + zw, xy = quaternion.i() * quaternion.j(), dxy = xy + xy;
        double xz = quaternion.i() * quaternion.k(), dxz = xz + xz, yw = quaternion.j() * quaternion.r(), dyw = yw + yw;
        double yz = quaternion.j() * quaternion.k(), dyz = yz + yz, xw = quaternion.i() * quaternion.r(), dxw = xw + xw;
        double rm00 = w2 + x2 - z2 - y2;
        double rm01 = dxy + dzw;
        double rm02 = dxz - dyw;
        double rm10 = dxy - dzw;
        double rm11 = y2 - z2 + w2 - x2;
        double rm12 = dyz + dxw;
        double rm20 = dyw + dxz;
        double rm21 = dyz - dxw;
        double rm22 = z2 - y2 - x2 + w2;
        double nm00 = m00 * rm00 + m10 * rm01 + m20 * rm02;
        double nm01 = m01 * rm00 + m11 * rm01 + m21 * rm02;
        double nm02 = m02 * rm00 + m12 * rm01 + m22 * rm02;
        double nm10 = m00 * rm10 + m10 * rm11 + m20 * rm12;
        double nm11 = m01 * rm10 + m11 * rm11 + m21 * rm12;
        double nm12 = m02 * rm10 + m12 * rm11 + m22 * rm12;
        
        this.m20 = m00 * rm20 + m10 * rm21 + m20 * rm22;
        this.m21 = m01 * rm20 + m11 * rm21 + m21 * rm22;
        this.m22 = m02 * rm20 + m12 * rm21 + m22 * rm22;
        this.m00 = nm00;
        this.m01 = nm01;
        this.m02 = nm02;
        this.m10 = nm10;
        this.m11 = nm11;
        this.m12 = nm12;
        
        return this;
    }
	
	public Vector3d transform(Vector3d vec)
	{
        double rx = Math.fma(this.m00, vec.x, Math.fma(this.m10, vec.y, this.m20 * vec.z));
        double ry = Math.fma(this.m01, vec.x, Math.fma(this.m11, vec.y, this.m21 * vec.z));
        double rz = Math.fma(this.m02, vec.x, Math.fma(this.m12, vec.y, this.m22 * vec.z));
        
        vec.x = rx;
        vec.y = ry;
        vec.z = rz;
        
        return vec;
    }
}
