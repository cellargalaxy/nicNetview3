package top.cellargalaxy.dao;

import org.apache.ibatis.annotations.*;
import top.cellargalaxy.bean.monitor.Equipment;
import top.cellargalaxy.bean.monitor.Place;

/**
 * Created by cellargalaxy on 17-12-7.
 */
@Mapper
public interface EquipmentMapper {
	@Insert("insert into equipment(id, model, name, buyDate, area, build, floor, number, ip, checkTimes, isWarn, remark, installDate) " +
			"values(#{id}, #{model}, #{name}, #{buyDate}, #{area}, #{build}, #{floor}, #{number}, #{ip}, #{checkTimes}, #{isWarn}, #{remark}, #{installDate})")
	int insertEquipment(Equipment equipment);
	
	@Delete("delete from equipment where id=#{id}")
	int deleteEquipment(@Param("id") String id);
	
	@Delete("delete from equipment where area=#{area} and build=#{build} and floor=#{floor} and number=#{number}")
	int deleteEquipmentByPlace(Place place);
	
	@Select("select * from equipment where id=#{id} limit 1")
	Equipment selectEquipmentById(@Param("id") String id);
	
	@Select("select * from equipment")
	Equipment[] selectAllEquipment();
	
	@Update("update equipment set model=#{model}, name=#{name}, buyDate=#{buyDate}, area=#{area}, build=#{build}, floor=#{floor}, number=#{number}, " +
			"ip=#{ip}, checkTimes=#{checkTimes}, isWarn=#{isWarn}, remark=#{remark}, installDate=#{installDate} where id=#{id}")
	int updateEquipment(Equipment equipment);
}
