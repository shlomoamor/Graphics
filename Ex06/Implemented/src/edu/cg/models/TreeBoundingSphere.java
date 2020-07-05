/**
 * Exercise Number : 	5
 * Date: 				16/06/2020
 * Name 1: 				Shlomo Amor 000803254
 * Name 2:				Dean Meyer  000802794
 *
 * */
package edu.cg.models;

import java.util.LinkedList;

public class TreeBoundingSphere {
    private BoundingSphere bSphere;
    private LinkedList<TreeBoundingSphere> sphereList = new LinkedList<TreeBoundingSphere>();
    public BoundingSphere getBoundingSphere() {
        return bSphere;
    }
    public void setBoundingSphere(BoundingSphere boundingSphere) {
        this.bSphere = boundingSphere;
    }
    public LinkedList<TreeBoundingSphere> getList() {
        return sphereList;
    }
    public void setList(LinkedList<TreeBoundingSphere> list) {
        this.sphereList = list;
    }
}
