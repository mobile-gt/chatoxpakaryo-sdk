package com.gamatechno.chato.sdk.app.grouproomdetail.dialog

import android.app.Dialog
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Gravity
import com.gamatechno.chato.sdk.R
import com.gamatechno.chato.sdk.app.grouproomdetail.dialog.adapter.OptionAdapter
import com.gamatechno.chato.sdk.app.grouproomdetail.helper.GroupDetailHelper
import com.gamatechno.chato.sdk.app.kontakchat.KontakModel
import com.gamatechno.chato.sdk.data.DAO.Group.Group
import com.gamatechno.chato.sdk.data.model.OptionModel
import com.gamatechno.ggfw.utils.DialogBuilder
import kotlinx.android.synthetic.main.dialog_group_action.*

class GroupActionDialog(context: Context?, group: Group, kontakModel: KontakModel, onOnGroupAction: OnGroupActionListener ) : DialogBuilder(context, R.layout.dialog_group_action) {

    var optionAdapter: OptionAdapter? = null
    var list: MutableList<OptionModel> = ArrayList()

    init {
        setAnimation(R.style.DialogBottomAnimation)
        setFullWidth(dialog.lay_dialog)
        setGravity(Gravity.BOTTOM)
        initComponent(group, kontakModel, onOnGroupAction)
        show()
    }

    fun initComponent(group: Group, kontakModel: KontakModel, onOnGroupAction: OnGroupActionListener){
        with(kontakModel){
            if(group.is_admin == 1){
                if(is_admin == 1){
                    list.addAll(GroupDetailHelper.list_foradmin(kontakModel.user_name))
                } else {
                    list.addAll(GroupDetailHelper.list_forotheradmin(kontakModel.user_name))
                }
            } else {
                list.addAll(GroupDetailHelper.list_foruser(kontakModel.user_name))
            }
        }

        optionAdapter = OptionAdapter(context, list, object : OptionAdapter.OnOptionAdapter{
            override fun onOptionClick(optionModel: OptionModel) {
                when(optionModel.id){
                    GroupDetailHelper.id_chat -> {
                        onOnGroupAction.onChat(dialog, kontakModel)
                    }
                    GroupDetailHelper.id_addtoadmin -> {
                        onOnGroupAction.onAddToAdmin(dialog, kontakModel)
                    }
                    GroupDetailHelper.id_deletemember -> {
                        onOnGroupAction.onDelete(dialog, kontakModel)
                    }
                    GroupDetailHelper.id_removefromadmin -> {
                        onOnGroupAction.onRemoveFromAdmin(dialog, kontakModel)
                    }
                }
            }
        })
        with(dialog){
            rv.layoutManager = LinearLayoutManager(context)
            rv.adapter = optionAdapter
        }
    }

    interface OnGroupActionListener {
        fun onChat(dialog: Dialog, kontakModel: KontakModel)
        fun onDelete(dialog: Dialog, kontakModel: KontakModel)
        fun onRemoveFromAdmin(dialog: Dialog, kontakModel: KontakModel)
        fun onAddToAdmin(dialog: Dialog, kontakModel: KontakModel)
    }
}
