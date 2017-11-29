package com.iteye.chenwh.wound.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "record_image")
public class RecordImage extends IdEntity {
	
	private ArchivesRecord record;
	private Long recordId;//记录id
	private String imageType;//图片类型
	private String imagePath;//图片路径
	private Date createTime;
	private Date updateTime;

	public Long getRecordId() {
		return recordId;
	}
	
	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}
	
	public String getImageType() {
		return imageType;
	}
	
	public void setImageType(String imageType) {
		this.imageType = imageType;
	}
	
	public String getImagePath() {
		return imagePath;
	}
	
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	public Date getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public Date getUpdateTime() {
		return updateTime;
	}
	
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	/* 
     * @ManyToOne指明OrderItem和Order之间为多对一关系，多个OrderItem实例关联的都是同一个Order对象。 
     * 其中的属性和@OneToMany基本一样，但@ManyToOne注释的fetch属性默认值是FetchType.EAGER。 
     *  
     * optional 属性是定义该关联类对是否必须存在，值为false时，关联类双方都必须存在，如果关系被维护端不存在，查询的结果为null。 
     * 值为true 时, 关系被维护端可以不存在，查询的结果仍然会返回关系维护端，在关系维护端中指向关系被维护端的属性为null。 
     * optional 属性的默认值是true。举个例：某项订单(Order)中没有订单项(OrderItem)，如果optional 属性设置为false， 
     * 获取该项订单（Order）时，得到的结果为null，如果optional 属性设置为true，仍然可以获取该项订单，但订单中指向订单项的属性为null。 
     * 实际上在解释Order 与OrderItem的关系成SQL时，optional 属性指定了他们的联接关系optional=false联接关系为inner join,  
     * optional=true联接关系为left join。 
     *  
     * @JoinColumn:指明了被维护端（OrderItem）的外键字段为order_id，它和维护端的主键(orderid)连接,unique= true 指明order_id列的值不可重复。
     * 注意：在ManyToOne中的 @JoinColumn(name = "order_id",referencedColumnName="orderid")，这里的name = "order_id"是orderitem的外键，与order表的主键关联，如果OrderItem类当中有个属性叫oder_id,那么就会报：
     *should be mapped with insert="false" updatable=false
     *这是要在 @JoinColumn(name = "order_id",referencedColumnName="orderid")加上insertable=false updatable=false以避免字段重复映射 
     */
	//@ManyToOne(cascade={CascadeType.MERGE,CascadeType.REFRESH})  
    //@JoinColumn(name="recordId", referencedColumnName="id", insertable=false, updatable=false) 
	@Transient
	public ArchivesRecord getRecord() {
		return record;
	}
	
	public void setRecord(ArchivesRecord record) {
		this.record = record;
	}
	
}
