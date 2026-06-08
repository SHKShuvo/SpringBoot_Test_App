package com.example.test.Repository;

import com.example.test.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("SELECT u From UserEntity u where u.actFlg = 'Y' ")
    public List<UserEntity> getAllUsers();

    Optional<UserEntity> findUserEntityById(long id);

    @Query(
            nativeQuery = true,
            value = "SELECT * from EMPLOYEES e WHERE e.DEPARTMENT_ID = :deptId"
    )
    List<Object[]> getDeptById(@Param("deptId") long deptId);

    @Query(
            nativeQuery = true,
            value = "WITH h (name,\n" +
                    "        child_id,\n" +
                    "        parent_id,\n" +
                    "        lvl,\n" +
                    "        is_leaf,\n" +
                    "        is_max_depth)\n" +
                    "     AS (    SELECT component_name name,\n" +
                    "                    unique_id child_id,\n" +
                    "                    parent_unique_id parent_id,\n" +
                    "                    LEVEL,\n" +
                    "                    CONNECT_BY_ISLEAF,\n" +
                    "                    CASE level_code WHEN MAX (level_code) OVER () THEN 1 END\n" +
                    "                       level_code\n" +
                    "               FROM (  SELECT a.parent_unique_id,\n" +
                    "                              a.level_code,\n" +
                    "                              a.component_name,\n" +
                    "                              a.unique_id,\n" +
                    "                              a.menu_serial\n" +
                    "                         FROM menu_master a\n" +
                    "                        WHERE a.application_unique_id = :applicationUniqueId\n" +
                    "                     ORDER BY menu_serial) a\n" +
                    "         START WITH parent_unique_id IS NULL\n" +
                    "         CONNECT BY parent_unique_id = PRIOR unique_id\n" +
                    "           ORDER BY unique_id, menu_serial),\n" +
                    "     r (lvl,\n" +
                    "        name,\n" +
                    "        child_id,\n" +
                    "        parent_id,\n" +
                    "        json_str,\n" +
                    "        rn)\n" +
                    "     AS (SELECT lvl,\n" +
                    "                name,\n" +
                    "                child_id,\n" +
                    "                parent_id,\n" +
                    "                   '{\"title\":\"'\n" +
                    "                || name\n" +
                    "                || '\",\"Child_Id\":'\n" +
                    "                || child_id\n" +
                    "                || ',\"submenu\":[]}',\n" +
                    "                1\n" +
                    "           FROM h\n" +
                    "          WHERE is_max_depth = 1\n" +
                    "         UNION ALL\n" +
                    "         SELECT h.lvl,\n" +
                    "                h.name,\n" +
                    "                h.child_id,\n" +
                    "                h.parent_id,\n" +
                    "                   '{\"title\":\"'\n" +
                    "                || h.name\n" +
                    "                || '\",\"Child_Id\":'\n" +
                    "                || h.child_id\n" +
                    "                || CASE\n" +
                    "                      WHEN is_leaf = 0\n" +
                    "                      THEN\n" +
                    "                            ',\"submenu\":['\n" +
                    "                         || LISTAGG (r.json_str, ',')\n" +
                    "                               WITHIN GROUP (ORDER BY r.name)\n" +
                    "                               OVER (PARTITION BY h.child_id)\n" +
                    "                         || ']'\n" +
                    "                      ELSE\n" +
                    "                         ',\"submenu\":[]'\n" +
                    "                   END\n" +
                    "                || '}',\n" +
                    "                ROW_NUMBER () OVER (PARTITION BY h.child_id ORDER BY NULL)\n" +
                    "           FROM h\n" +
                    "                JOIN r\n" +
                    "                   ON     h.lvl = r.lvl - 1\n" +
                    "                      AND r.rn = 1\n" +
                    "                      AND (h.is_leaf = 1 OR h.child_id = r.parent_id))\n" +
                    "SELECT json_str\n" +
                    "  FROM r\n" +
                    " WHERE lvl = 1 AND rn = 1"
    )
    Object menuTreePopulate(@Param("applicationUniqueId") long applicationUniqueId);

}
