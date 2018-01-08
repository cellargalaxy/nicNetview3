package top.cellargalaxy.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.cellargalaxy.bean.monitor.Place;

/**
 * Created by cellargalaxy on 17-12-7.
 */
@Mapper
public interface PlaceMapper {
	@Insert("insert into place(area, build, floor, number) values(#{area}, #{build}, #{floor}, #{number})")
	int insertPlace(Place place);
	
	@Delete("delete from place where area=#{area} and build=#{build} and floor=#{floor} and number=#{number}")
	int deletePlace(Place place);
	
	@Select("select * from place")
	Place[] selectAllPlace();
}
