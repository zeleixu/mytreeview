package com.xuzelei.tree;

import java.util.ArrayList;
import java.util.List;

import android.util.AttributeSet;
//import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;

/**
 * 自定义树形组件
 * @author 252386922@qq.com
 *
 */
public class TreeView extends LinearLayout {
	/** 树的初始节点集合. */
	public List<TreeNode> Nodes;
	
	/** 是否显示复选框的节点类型. */
	public boolean ShowCheckBoxes=false;
	
	/** 默认节点的图片. */
	public int NoExpandImage=0;
	
	/** 展开节点的图片. */
	public int ExpandImage=0;
	
	/** 复选框勾选的图片. */
	public int CheckOnImage=0;
	
	/** 复选框未勾选的图片. */
	public int CheckOffImage=0;
	
	/** 节点的文字颜色. */
	public int NodeColor=0;
	
	/** 字体大小*/
	public float TextSize = 0;
	
	/** 是否显示节点图标*/
	public boolean isShowIcon;	
	/** 设置导航节点、标识和勾选框之间的距离，默认为2*/
	public int Margin=2;
	/** 是否显示交替背景颜色*/
	public boolean ShowItemBackGroundColor;
	/** 交替背景颜色的值，当ShowItemColor=true时才有效*/
	public int[] ItemBackgroundColor;	
	/** 当展开节点时，展开新行的左缩进*/
	public int MarginLeftOnRowExpand=70;
	/** 递归展开时，新节点应插入的索引位置，改值为单击节点索引值+1+已累计增加的数量*/
	private int AddIndex = 0;
	/** 递归勾选时，新节点的索引位置，改值为勾选节点索引值+1+已累计勾选的数量*/
	private int checkedIndex = 0;	
	
	private Context OfActivity;
	private OnNodeClickListener NodeClickListener;
	private OnNodeCheckClickListener NodeCheckClickListener;
	
	public TreeView(Context context) {
		super(context);
		OfActivity=context;
        this.setOrientation( LinearLayout.VERTICAL ); //垂直排列子控件        
	}
	public TreeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		OfActivity=context;
        this.setOrientation( LinearLayout.VERTICAL ); //垂直排列子控件
	}
	/**
	 * 获取勾选节点集合
	 * @return
	 */
	public List<TreeNode> getCheckNodes() {
		List<TreeNode> CheckNodes = new ArrayList<TreeNode>();
		if(null !=Nodes&&Nodes.size()>0) {
			for(TreeNode item:Nodes)
	    	{ 
	    		if(item.GetIsChecked()) {
	    			CheckNodes.add(item);//CheckNodes.addElement(item);
	    		}
	    	}  
		}		
		return CheckNodes;
	}
	/**
	 * 节点勾选单击事件
	 * @param NodeCheckClickListener
	 */
	public void setOnNodeCheckClickListener(OnNodeCheckClickListener NodeCheckClickListener) {
		this.NodeCheckClickListener=NodeCheckClickListener;
	}
	/**
	 * 节点单击事件
	 * @param NodeClickListener
	 */
	public void setOnNodeClickListener(OnNodeClickListener NodeClickListener) {
		this.NodeClickListener=NodeClickListener;
	}
	/**
	 * 渲染树
	 */
	public void Render(String RootNodeParentID)
	{
		List<TreeNode> tmpVector = GetChildsTreeNode(RootNodeParentID);
        createTree(OfActivity,tmpVector,this,1);//递归建立树          
	}
	/**
	 * 递归实现树的渲染
	 * @param context 上下文对象
	 * @param tmpVector 需要渲染的节点集合
	 * @param layout 需要被加入的layout
	 * @param lay 层数
	 */
	private void createTree(Context context,List<TreeNode> tmpVector,LinearLayout layout,int lay)
    { 		
		layout.setGravity(Gravity.CENTER_VERTICAL);
    	if(tmpVector.size()>0)
    	{     		
    		for(TreeNode item:tmpVector)
    		{
    			//-------------相关数据--------------    			     			
    			List<TreeNode> itemChildNodes = GetChildsTreeNode(item.getId());    			
    			//--------------层信息------------------
    			LinearLayout layouttmp = new LinearLayout(context);
    			layouttmp.setTag(lay);//保存所在的层级别   tag=level
    			layouttmp.setGravity(Gravity.CENTER_VERTICAL);
    			layouttmp.setOrientation(HORIZONTAL);
    			layouttmp.setPadding(5, 20, 5, 20);
    			if(ShowItemBackGroundColor) {
    				if((layout.getChildCount()&1)==0) {
        				layouttmp.setBackgroundColor(ItemBackgroundColor[0]);
        			}
    				else {
    					layouttmp.setBackgroundColor(ItemBackgroundColor[1]);
    				}
    			}
    			
//    			Log.i("treeview", "节点  "+item.getName() +"所在Layout的tag是"+String.valueOf(lay));
    			//--------------导航接点-----------------
    			if(this.ExpandImage==0) {
	    			TextView V_navigate = new TextView(context);
	    			V_navigate.setTag(item.getId());    	        
	    			if(NodeColor!=0) V_navigate.setTextColor(NodeColor);
	    			V_navigate.setClickable(true);
	    			V_navigate.setFocusable(true);
	    			if(!item.Getisextends())
	    			{    			
	    				if(itemChildNodes.size()>0) {
	    					V_navigate.setText("+");
	    				}
	    				else {
	    					V_navigate.setText("-");
	    				}    				
	    			}
	    			else
	    			{		
	    				V_navigate.setText("-");
	    			}
	    			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    				params.leftMargin = lay*MarginLeftOnRowExpand;
    				V_navigate.setLayoutParams(params);
//	    	        V_navigate.setPadding(0, 12, 0, 0);
	    	        V_navigate.setOnClickListener(new View.OnClickListener()
	    			{
	    				public void onClick(View v)
	    	        	{
	    					TreeNode item = getByTag(v.getTag().toString());    					
	    					
	    					if(item.Getisextends())//如果当前已经展开下级，递归收缩下级，并改变当前的isextends
	    					{
	    						//递归收缩下级
								LinearLayout cur = (LinearLayout)v.getParent();
								LinearLayout z = (LinearLayout)cur.getParent();
								int Layoutindex = z.indexOfChild(cur);
	    						
								int childsize = dgss(v.getTag().toString(),Layoutindex,z);
								if(childsize>0) 
								{
									((TextView)v).setText("+");
									
									//改变当前的isextends
		    						item.Setisextends(false);
		    						int index = Nodes.indexOf(item);
		    						Nodes.set(index, item);//Nodes.setElementAt(item, index);
								}    						
	    					}
							else//如果当前没有展开下级，递归展开下级，并改变当前的isextends
							{
								//递归展开下级
								LinearLayout cur = (LinearLayout)v.getParent();
								LinearLayout parentLinearLayout = (LinearLayout)cur.getParent();
								int Layoutindex = parentLinearLayout.indexOfChild(cur);
								    			
								AddIndex = Layoutindex+1;
								int childsize = dgzk(OfActivity,v.getTag().toString(),parentLinearLayout,Integer.parseInt(cur.getTag().toString())+1);
								if(childsize>0) 
								{
									((TextView)v).setText("-");
									
									//改变当前的isextends
		    						item.Setisextends(true);
		    						int index = Nodes.indexOf(item);
		    						Nodes.set(index, item);//Nodes.setElementAt(item, index);
								}							
							}
	    	        	}
	    			});
	    	        layouttmp.addView(V_navigate);
    			}
    			else
    			{
    				ImageView V_navigate = new ImageView(context);
    				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    				params.leftMargin = lay*MarginLeftOnRowExpand;
    				V_navigate.setLayoutParams(params);
//    				V_navigate.setPadding(0, 12, 0, 0);
    				V_navigate.setTag(item.getId());
    				
    				if(!item.Getisextends())
        			{    			
        				if(itemChildNodes.size()>0) 
        				{
        					V_navigate.setImageResource(this.ExpandImage);
        				}
        				else
        				{
        					V_navigate.setImageResource(this.NoExpandImage);
        				}    				
        			}
        			else
        			{		
        				V_navigate.setImageResource(this.NoExpandImage);
        			}
    				
    				V_navigate.setOnClickListener(new View.OnClickListener()
	    			{
	    				public void onClick(View v)
	    	        	{ 
	    					TreeNode item = getByTag(v.getTag().toString());    					
	    					
	    					if(item.Getisextends())//如果当前已经展开下级，递归收缩下级，并改变当前的isextends
	    					{
	    						//递归
								LinearLayout cur = (LinearLayout)v.getParent();
								LinearLayout parentLinearLayout = (LinearLayout)cur.getParent();
								int Layoutindex = parentLinearLayout.indexOfChild(cur);
	    						
								int childsize = dgss(v.getTag().toString(),Layoutindex,parentLinearLayout);
								if(childsize>0) 
								{
									((ImageView)v).setImageResource(ExpandImage);
									
									//收缩
		    						item.Setisextends(false);
		    						int index = Nodes.indexOf(item);
		    						Nodes.set(index, item);//Nodes.setElementAt(item, index);
								}    						
	    					}
							else//如果当前没有展开下级，递归展开下级，并改变当前的isextends
							{
								//递归
								LinearLayout cur = (LinearLayout)v.getParent();
								LinearLayout parentLinearLayout = (LinearLayout)cur.getParent();
								int Layoutindex = parentLinearLayout.indexOfChild(cur);
								    			
								AddIndex = Layoutindex+1;
								int childsize = dgzk(OfActivity,v.getTag().toString(),parentLinearLayout,Integer.parseInt(cur.getTag().toString())+1);
								if(childsize>0) 
								{
									((ImageView)v).setImageResource(NoExpandImage);
									
									//展开
		    						item.Setisextends(true);
		    						int index = Nodes.indexOf(item);
		    						Nodes.set(index, item);//Nodes.setElementAt(item, index);
								}							
							}
	    	        	}
	    			});
	    	        layouttmp.addView(V_navigate);
    			}
    			//---------------标识节点图片-----------------
    			if(isShowIcon) {
    				ImageView typeImg = new ImageView(context); 
    				typeImg.setImageResource(item.getIcon());
            		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    				params.leftMargin = Margin;
    				typeImg.setLayoutParams(params);
            		layouttmp.addView(typeImg);
    			}   
    			//--------------check图片----------------
    			if(this.ShowCheckBoxes) {    				
	    			ImageView btn = new ImageView(context); 
	    			btn.setTag(item.getId());
//	    			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//    				params.leftMargin = Margin;
//    				btn.setLayoutParams(params);
	    			btn.setPadding(Margin, 0, 0, 0);
	    			if(item.GetIsChecked())
	    			{      
	    				btn.setImageResource(this.CheckOnImage);
	    			}
	    			else
	    			{		
	    				btn.setImageResource(this.CheckOffImage); 
	    			}    	
	    			
	    			btn.setOnClickListener(new View.OnClickListener()
	    			{
	    				public void onClick(View v)
	    	        	{  
	    					TreeNode item = getByTag(v.getTag().toString()); 
	    					if(item.GetIsChecked())
	    					{
	    						item.SetIsChecked(false);
	    						((ImageView)v).setImageResource(CheckOffImage);
	    					}
	    					else
	    					{
	    						item.SetIsChecked(true);
	    						((ImageView)v).setImageResource(CheckOnImage);
	    					}
	    					int index = Nodes.indexOf(item);
	    					Nodes.set(index, item);//Nodes.setElementAt(item, index);
	    					
	    					//递归选择下级节点
	    					LinearLayout cur = (LinearLayout)v.getParent();
							LinearLayout z = (LinearLayout)cur.getParent();
							int Layoutindex = z.indexOfChild(cur);
							checkedIndex = Layoutindex+1;
							dgchecked(v.getTag().toString(),z,item.GetIsChecked(),item.Getisextends());
							dgcheckedchangeitem(v.getTag().toString(),item.GetIsChecked());
							
	    					if(NodeCheckClickListener!=null) {
	    						NodeCheckClickListener.OnNodeCheckClick(item);     	        			
        	        		}   
	    					
	    	        	}
	    			});
	    			
	    			layouttmp.addView(btn, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    			}
    			//--------------显示文本---------------
    	        TextView tv = new TextView(context);   
    	        tv.setTag(item.getId());
    	        tv.setText( item.getName() ); 
    	        tv.setGravity(Gravity.CENTER_VERTICAL);
    	        if(TextSize!=0) tv.setTextSize(TextSize);
    	        if(NodeColor!=0) tv.setTextColor(NodeColor);
    	        
    	        tv.setPadding(5, 0, 0, 0);
    	        tv.setClickable(true);
    	        tv.setFocusable(true);
    	        //节点单击事件
    	        tv.setOnClickListener(new View.OnClickListener()
    	        {
    	        	public void onClick(View v)
    	        	{ 
    	        		if(NodeClickListener!=null) {    	        			
    	        			TreeNode item = getByTag(v.getTag().toString()); 
    	        			NodeClickListener.onNodeClick(item);     	        			
    	        		}   	        		     	        		
    	        	}
	        	});
    	        /*
    	        tv.setOnFocusChangeListener(new View.OnFocusChangeListener()
    	        {
    	        	public void onFocusChange(View v,boolean isfocus)
    	        	{
    	        		TextView tv = (TextView)v;
    	        		if(isfocus)
    	        		{
    	        			tv.setTextColor(Color.rgb(0, 0, 255));
    	        		}
    	        		else
    	        		{	
    	        			tv.setTextColor(Color.rgb(255, 255, 255));
    	        		}    	        		
    	        	}
    	        }); */
    	        layouttmp.addView(tv);
    	        //-----------------
    	        layout.addView(layouttmp);
    	        if(item.Getisextends())
    	        {
    	        	createTree(context,itemChildNodes,layout,lay+1);
    	        }
    		}		
    	}    	   
    }
	/**
	 * 点击图片时，递归展开其子节点
	 * @param context 上下文对象，用来动态建立控件
	 * @param itemid 当前节点的ID值
	 * @param z 整个最外部的layout
	 * @param lay 新展开的节点的层数，用来处理left padding
	 * @return 一级子节点的个数
	 */
    private int dgzk(Context context,String itemid,LinearLayout z,int lay)//递归展开
    {
    	List<TreeNode> aa = GetChildsTreeNode(itemid);
    	if(aa!=null && aa.size()>0) {
    		for(TreeNode item : aa)
    		{    		
        		//-------------相关数据--------------    		
        		List<TreeNode> itemChildNodes = GetChildsTreeNode(item.getId());
        		//--------------层信息------------------
        		LinearLayout layouttmp = new LinearLayout(context);
        		layouttmp.setTag(lay); 
        		layouttmp.setGravity(Gravity.CENTER_VERTICAL);
        		layouttmp.setOrientation(HORIZONTAL);
        		layouttmp.setPadding(5, 20, 5, 20);
        		if(ShowItemBackGroundColor) {
    				if((z.getChildCount()&1)==0) {
        				layouttmp.setBackgroundColor(ItemBackgroundColor[0]);
        			}
    				else {
    					layouttmp.setBackgroundColor(ItemBackgroundColor[1]);
    				}
    			}
        		//--------------导航接点-----------------
        		if(this.ExpandImage==0) {
        			TextView V_navigate = new TextView(context);
        			V_navigate.setTag(item.getId());    	        
        			if(NodeColor!=0) V_navigate.setTextColor(NodeColor);
        			V_navigate.setClickable(true);
        			V_navigate.setFocusable(true);
        	        if(!item.Getisextends())
        			{    			
        				if(itemChildNodes.size()>0) {
        					V_navigate.setText("+");
        				}
        				else {
        					V_navigate.setText("-");
        				}    				
        			}
        			else
        			{		
        				V_navigate.setText("-");
        			}
        	        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        	        params.leftMargin = lay*MarginLeftOnRowExpand;
        	        V_navigate.setLayoutParams(params);
//        	        V_navigate.setPadding(0, 12, 0, 0);
        	        V_navigate.setOnClickListener(new View.OnClickListener()
        			{
        				public void onClick(View v)
        	        	{ 
        					TreeNode item = getByTag(v.getTag().toString());    					
        					
        					if(item.Getisextends())//如果当前已经展开下级，递归收缩下级，并改变当前的isextends
        					{
        						//递归
    							LinearLayout cur = (LinearLayout)v.getParent();
    							LinearLayout z = (LinearLayout)cur.getParent();
    							int Layoutindex = z.indexOfChild(cur);
        						
    							int childsize = dgss(v.getTag().toString(),Layoutindex,z);
    							if(childsize>0) 
    							{
    								((TextView)v).setText("+");
    								
    								//收缩
    	    						item.Setisextends(false);
    	    						int index = Nodes.indexOf(item);
    	    						Nodes.set(index, item);//Nodes.setElementAt(item, index);
    							}    						
        					}
    						else//如果当前没有展开下级，递归展开下级，并改变当前的isextends
    						{
    							//递归
    							LinearLayout cur = (LinearLayout)v.getParent();
    							LinearLayout z = (LinearLayout)cur.getParent();
    							int Layoutindex = z.indexOfChild(cur);
    							    			
    							AddIndex = Layoutindex+1;
    							int childsize = dgzk(OfActivity,v.getTag().toString(),z,Integer.parseInt(cur.getTag().toString())+1);
    							if(childsize>0) 
    							{
    								((TextView)v).setText("-");
    								
    								//展开
    	    						item.Setisextends(true);
    	    						int index = Nodes.indexOf(item);
    	    						Nodes.set(index, item);//Nodes.setElementAt(item, index);
    							}							
    						}
        	        	}
        			});
        	        layouttmp.addView(V_navigate);
    			}
    			else
    			{
    				ImageView V_navigate = new ImageView(context); 
    				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        	        params.leftMargin = lay*MarginLeftOnRowExpand;
        	        V_navigate.setLayoutParams(params);
//    				V_navigate.setPadding(0, 12, 0, 0);
    				V_navigate.setTag(item.getId());
    				
    				if(!item.Getisextends())
        			{    			
        				if(itemChildNodes.size()>0) 
        				{
        					V_navigate.setImageResource(this.ExpandImage);
        				}
        				else
        				{
        					V_navigate.setImageResource(this.NoExpandImage);
        				}    				
        			}
        			else
        			{		
        				V_navigate.setImageResource(this.NoExpandImage);
        			}
    				
    				V_navigate.setOnClickListener(new View.OnClickListener()
        			{
        				public void onClick(View v)
        	        	{  
        					TreeNode item = getByTag(v.getTag().toString());    					
        					
        					if(item.Getisextends())//如果当前已经展开下级，递归收缩下级，并改变当前的isextends
        					{
        						//递归
    							LinearLayout cur = (LinearLayout)v.getParent();
    							LinearLayout z = (LinearLayout)cur.getParent();
    							int Layoutindex = z.indexOfChild(cur);
        						
    							int childsize = dgss(v.getTag().toString(),Layoutindex,z);
    							if(childsize>0) 
    							{
    								((ImageView)v).setImageResource(ExpandImage);
    								
    								//收缩
    	    						item.Setisextends(false);
    	    						int index = Nodes.indexOf(item);
    	    						Nodes.set(index, item);//Nodes.setElementAt(item, index);
    							}    						
        					}
    						else//如果当前没有展开下级，递归展开下级，并改变当前的isextends
    						{
    							//递归
    							LinearLayout cur = (LinearLayout)v.getParent();
    							LinearLayout z = (LinearLayout)cur.getParent();
    							int Layoutindex = z.indexOfChild(cur);
    							    			
    							AddIndex = Layoutindex+1;
    							int childsize = dgzk(OfActivity,v.getTag().toString(),z,Integer.parseInt(cur.getTag().toString())+1);
    							if(childsize>0) 
    							{
    								((ImageView)v).setImageResource(NoExpandImage);
    								
    								//展开
    	    						item.Setisextends(true);
    	    						int index = Nodes.indexOf(item);
    	    						Nodes.set(index, item);//Nodes.setElementAt(item, index);
    							}							
    						}
        	        	}
        			});
        	        layouttmp.addView(V_navigate);
    			}
        		//---------------标识节点图片-----------------
    			if(isShowIcon) {
    				ImageView typeImg = new ImageView(context); 
    				typeImg.setImageResource(item.getIcon());
            		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    				params.leftMargin = Margin;
    				typeImg.setLayoutParams(params);
            		layouttmp.addView(typeImg);
    			}   
    	      //--------------check图片----------------
        		if(this.ShowCheckBoxes) {    
    				ImageView btn = new ImageView(context); 
    				btn.setTag(item.getId());
//    				btn.setPadding(2, 12, 0, 0);
    				btn.setPadding(Margin, 0, 0, 0);
//    				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//    				params.leftMargin = Margin;
//    				btn.setLayoutParams(params);
    				
    				if(item.GetIsChecked())
    				{ 
    					btn.setImageResource(this.CheckOnImage); 
    				}
    				else
    				{		
    					btn.setImageResource(this.CheckOffImage); 
    				}    	
    				
    				btn.setOnClickListener(new View.OnClickListener()
    				{
    					public void onClick(View v)
    		        	{   
    						TreeNode item = getByTag(v.getTag().toString()); 
    						if(item.GetIsChecked())
    						{
    							item.SetIsChecked(false);
    							((ImageView)v).setImageResource(CheckOffImage);
    						}
    						else
    						{
    							item.SetIsChecked(true);
    							((ImageView)v).setImageResource(CheckOnImage);
    						}
    						int index = Nodes.indexOf(item);
    						Nodes.set(index, item);//Nodes.setElementAt(item, index);
    						
    						//递归选择下级节点
        					LinearLayout cur = (LinearLayout)v.getParent();
    						LinearLayout z = (LinearLayout)cur.getParent();
    						int Layoutindex = z.indexOfChild(cur);
    						checkedIndex = Layoutindex+1;
    						dgchecked(v.getTag().toString(),z,item.GetIsChecked(),item.Getisextends());
    						
							dgcheckedchangeitem(v.getTag().toString(),item.GetIsChecked());
    						if(NodeCheckClickListener!=null) {
        						NodeCheckClickListener.OnNodeCheckClick(item);     	        			
        	        		} 
    		        	}
    				});
    				layouttmp.addView(btn, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        		}
    			//--------------显示文本---------------
    			TextView tv = new TextView(context);   
    	        tv.setTag(item.getId());
    	        tv.setText( item.getName() );  
    	        tv.setGravity(Gravity.CENTER_VERTICAL);
    	        if(TextSize!=0) tv.setTextSize(TextSize);
    	        if(NodeColor!=0)  tv.setTextColor(this.NodeColor);
    	        tv.setPadding(5, 0, 0, 0);
    	        tv.setClickable(true);
    	        tv.setFocusable(true);
    	        //节点单击事件
    	        tv.setOnClickListener(new View.OnClickListener()
    	        {
    	        	public void onClick(View v)
    	        	{ 
    	        		if(NodeClickListener!=null) {
    	        			TreeNode item = getByTag(v.getTag().toString()); 
    	        			NodeClickListener.onNodeClick(item);     	        			
    	        		}       	        		     	        		
    	        	}
            	});        
    	        /*tv.setOnFocusChangeListener(new View.OnFocusChangeListener()
    	        {
    	        	public void onFocusChange(View v,boolean isfocus)
    	        	{
    	        		TextView tv = (TextView)v;
    	        		if(isfocus)
    	        		{
    	        			tv.setTextColor(Color.rgb(0, 0, 255));
    	        		}
    	        		else
    	        		{	
    	        			tv.setTextColor(Color.rgb(255, 255, 255));
    	        		}    	        		
    	        	}
    	        }); */
    	        layouttmp.addView(tv);
    	        //--------------------------------------	
    	        z.addView(layouttmp, AddIndex);
//    	        layouttmp.setBackgroundColor(Color.argb(200, 200, 8, 7));
    	        AddIndex ++;
        		
        		if(item.Getisextends())
        		{
        			dgzk(context,item.getId(),z,lay+1);
        		}
    		}
    	}
    	
    	return aa.size();
    }
    /**
     * 递归勾选下级节点
     * @param itemid 当前勾选的itemid
     * @param z 整体layout
     * @param ischecked 是否勾选状态
     * @param isExtends 是否展开状态
     */
    private void dgchecked(String itemid,LinearLayout z,boolean ischecked,boolean isExtends)//递归勾选
    {
    	//如果下级节点没有展开，直接返回
    	if(!isExtends) return;
    	//递归勾选节点
    	List<TreeNode> aa = GetChildsTreeNode(itemid);
    	if(aa!=null && aa.size()>0) {
    		for(TreeNode item : aa)
    		{			
    			LinearLayout curr = (LinearLayout)z.getChildAt(checkedIndex);
    			
    			if(ischecked)
    			{
    				if(isShowIcon) {
    					((ImageView)curr.getChildAt(2)).setImageResource(this.CheckOnImage);
        			}   
    				else {
    					((ImageView)curr.getChildAt(1)).setImageResource(this.CheckOnImage);
    				}
    			}
    			else
    			{
    				if(isShowIcon) {
    					((ImageView)curr.getChildAt(2)).setImageResource(this.CheckOffImage);
        			}   
    				else {
    					((ImageView)curr.getChildAt(1)).setImageResource(this.CheckOffImage);
    				}
    			}
    			
    			checkedIndex ++;
    			dgchecked(item.getId(),z,ischecked, item.Getisextends());
    		}
    	}
    }
    private void dgcheckedchangeitem(String itemid, boolean ischecked) {
    	List<TreeNode> aa = GetChildsTreeNode(itemid);
    	if(aa!=null && aa.size()>0) {
    		for(TreeNode item : aa)	{		
    			item.SetIsChecked(ischecked);
    			dgcheckedchangeitem(item.getId(),ischecked);
    		}
    	}
    }
    private int dgss(String itemid,int Layoutindex,LinearLayout z)//递归收缩
    {
    	List<TreeNode> aa = GetChildsTreeNode(itemid);
    	if(aa!=null && aa.size()>0) {
    		for(TreeNode item : aa) {
    			z.removeViewAt(Layoutindex+1);	
    			if(item.Getisextends())
    			{
    				dgss(item.getId(),Layoutindex,z);
    			}
    		}
//    		for(int i=0;i<aa.size();i++)
//    		{
//    			TreeNode item = aa.get(i);//(TreeNode) aa.elementAt(i);
////    			Log.i("treeview", "收缩  "+item.getName());
//    			z.removeViewAt(Layoutindex+1);	
//    			if(item.Getisextends())
//    			{
//    				dgss(item.getId(),Layoutindex,z);
//    			}
//    		}
    	}
		
		return aa.size();
    }
    private TreeNode getByTag(String tag)
    {
    	if(Nodes.size()>0) {
    		for(TreeNode node:Nodes) {
        		if(node.getId().equals(tag)) return node;
        	}
    	}
    	return null;
    }
    private List<TreeNode> GetChildsTreeNode(String Pid)
    {
    	List<TreeNode> tmpVector = new ArrayList<TreeNode>();
    	if(Nodes.size()>0) {
    		for(TreeNode item : Nodes)
        	{   
        		if(item.getParentNodeId().equals(Pid))
        		{
        			tmpVector.add(item);
        		}
        	}
    	}
    	return tmpVector;
    }	
}
