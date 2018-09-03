/**
 * Copyright (C) 2013-2016 ECNU_DaSE.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * @file ob_procedure_static_data_mgr.h
 * @brief the ObProcedureStaticDataMgr class definition
 *
 * Created by zhutao
 *
 * @version __DaSE_VERSION
 * @author zhutao <zhutao@stu.ecnu.edu.cn>
 * @author wangdonghui <zjnuwangdonghui@163.com>
 *
 * @date 2016_07_28
 */

#ifndef OBPROCEDURESTATICDATAMGR_H
#define OBPROCEDURESTATICDATAMGR_H

#include "common/hash/ob_hashmap.h"
#include "common/ob_row_store.h"
#include "common/ob_se_array.h"
using namespace oceanbase::common;
using namespace oceanbase::common::hash;

namespace oceanbase
{
  namespace sql
  {

    /**
     * @brief The ObProcedureStaticDataMgr class
     * Manage static data used in procedure running.
     * It keeps all static data that being used for group execution
     */
    class ObProcedureStaticDataMgr
    {
      public:
        /**
         * @brief constructor
         */
        ObProcedureStaticDataMgr();
        /**
         * @brief init
         * initialize ObProcedureStaticDataMgr object
         * @return
         */
        int init();
        /**
         * @brief store
         * store static data
         * @param sdata_id static data id
         * @param hkey the hash value of the iteration number of the corresponding cycle
         * @param p_row_store ObRowStore object pointer
         * @return error code
         */
        int store(int64_t sdata_id, int64_t hkey, ObRowStore *&p_row_store);
        /**
         * @brief get
         * read static data
         * @param sdata_id static data id
         * @param hkey the hash value of the iteration number of the corresponding cycle
         * @param p_row_store  ObRowStore object pointer
         * @return error code
         */
        int get(int64_t sdata_id, int64_t hkey, ObRowStore *&p_row_store);
        /**
         * @brief get
         * read static data
         * @param idx static data store array index
         * @param sdata_id  static data id
         * @param hkey the hash value of the iteration number of the corresponding cycle
         * @param p_row_store  ObRowStore object pointer
         * @return  error code
         */
        int get(int64_t idx, int64_t &sdata_id, int64_t &hkey, const ObRowStore *&p_row_store) const;
        /**
         * @brief get_static_data_count
         * get static data store array size
         * @return static data store array size
         */
        int64_t get_static_data_count() const;
        /**
         * @brief clear
         * clear object
         * @return error code
         */
        int clear();

      private:
        /**
         * @brief The StaticData struct
         * static data
         */
        struct StaticData
        {
            int64_t id;   ///<  id is the only identifier that is generated at compile time, referring to a baseline read instruction.
                          ///<  all base line data generated by this instruction share the same id.
            int64_t hkey;  ///<  hkey is used to distinguish the baseline data generated by the same instruction in different loop iterations.
                           ///< according to the calculation of the number of iterations of the hkey generation.
                           ///< for nested loops, a hkey is generated for all iterations.
            ObRowStore store;  ///<  baseline data
        };

        ModuleArena static_store_arena_;  ///< static data memery allocator
        ObSEArray<StaticData*, 64> static_store_;  ///<  static data store array
        hash::ObHashMap<int64_t, int64_t> hkey_idx_map_;  ///<  hkey index map
    };
  }
}


#endif // OBPROCEDURESTATICDATAMGR_H
