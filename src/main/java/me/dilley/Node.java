/*
 * Linulator - The Linux Simulator
 * Copyright (C) 2014 Lloyd Dilley
 * http://www.linulator.org/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package me.dilley;

import java.util.ArrayList;
import java.util.Arrays;
//import java.util.List;

public class Node
{
  Node parent = null;
  ArrayList<Node> children = null;
  //List<Node> leaves = null;
  //String data = null;
  File file = null;
  //String incrementalPath = null;

  //public Node(String nodeValue, String incrementalPath)
  public Node(File file)
  {
    this.file = file;
    children = new ArrayList<Node>();
    //leaves = new ArrayList<Node>();
    //data = nodeValue;
    //this.incrementalPath = incrementalPath;
  }

  public boolean doesExist(String path)
  {
    return false;
  }

  public boolean isLeaf()
  {
    //return children.isEmpty() && leaves.isEmpty();
    return children.isEmpty();
  }

  public void add(File file)
  {
    String[] path = file.getPath().split("/");
    while(path[0] == null || path[0].equals(""))
      path = Arrays.copyOfRange(path, 1, path.length);

    //Node currentChild = new Node(path[0], currentPath + '/' + path[0]);
    Node currentChild = new Node(file);
    if(path.length == 1)
    {
      children.add(currentChild);
      return;
    }
    else
    {
      int index = children.indexOf(currentChild);
      if(index == -1)
      {
        children.add(currentChild);
        //    ----currentChild.add(file.getPath(), Arrays.copyOfRange(path, 1, path.length));
        //currentChild.add(currentChild.incrementalPath, Arrays.copyOfRange(path, 1, path.length));
      }
      else
      {
        Node nextChild = children.get(index);
        //   ----nextChild.add(file.getPath(), Arrays.copyOfRange(path, 1, path.length));
        //nextChild.add(currentChild.incrementalPath, Arrays.copyOfRange(path, 1, path.length));
      }
    }
  }
//  public void add(String currentPath, String[] path)
//  {
//    // Avoid first element that can be an empty string if you split a string that has a starting slash as /sd/card/
//    while(path[0] == null || path[0].equals(""))
//      path = Arrays.copyOfRange(path, 1, path.length);

//    Node currentChild = new Node(path[0], currentPath + "/" + path[0]);
//    if(path.length == 1)
//    {
//      leaves.add(currentChild);
//      return;
//    }
//    else
//    {
//      int index = children.indexOf(currentChild);
//      if(index == -1)
//      {
//        children.add(currentChild);
//        currentChild.add(currentChild.incrementalPath, Arrays.copyOfRange(path, 1, path.length));
//      }
//      else
//      {
//        Node nextChild = children.get(index);
//        nextChild.add(currentChild.incrementalPath, Arrays.copyOfRange(path, 1, path.length));
//      }
//    }
//  }

  //@Override
  //public boolean equals(Object obj)
  //{
  //  Node cmpObj = (Node)obj;
  //  return incrementalPath.equals(cmpObj.incrementalPath) && data.equals(cmpObj.data);
  //}

  public void showNode(int increment)
  {
    for(int i = 0; i < increment; i++)
    {
      System.out.print(" ");
    }
    //   ------System.out.println(incrementalPath + (isLeaf() ? " -> " + file.getPath() : ""));
    //System.out.println(incrementalPath + (isLeaf() ? " -> " + data : ""));
    for(Node n: children)
      n.showNode(increment + 2);
    //for(Node n: leaves)
    //  n.showNode(increment + 2);
  }

  //@Override
  //public String toString()
  //{
  //  return data;
  //}
}
