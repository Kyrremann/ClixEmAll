package net.fifthfloorstudio.gotta.clix.em.all;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

public class GalleryAdapter extends BaseAdapter {

	private Context context;

	public GalleryAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return thumbs.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageButton imageView;

		if (convertView == null) {
			// imageView = new ImageView(context);
			imageView = new ImageButton(context);
			imageView.setLayoutParams(new GridView.LayoutParams(
					GridLayout.LayoutParams.WRAP_CONTENT,
					GridLayout.LayoutParams.WRAP_CONTENT));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			// imageView.setPadding(8, 8, 8, 8);
		} else {
			imageView = (ImageButton) convertView;
		}

		imageView.setImageResource(thumbs[position]);
		imageView.setBackgroundResource(0);
		imageView.setClickable(false);
		imageView.setFocusable(false);
		return imageView;
	}

	private Integer[] thumbs = {
			// Modern
			R.drawable.set_wizkids, R.drawable.set_dp, R.drawable.set_dofp,
			R.drawable.set_fflod, R.drawable.set_slosh, R.drawable.set_iim, 
			R.drawable.set_avx,	R.drawable.set_bao, R.drawable.set_fi, 
			R.drawable.set_tdw, R.drawable.set_smqs, R.drawable.set_bctv,
			R.drawable.set_wxm, R.drawable.set_mos, R.drawable.set_tt,
			R.drawable.set_fftt, R.drawable.set_nml, R.drawable.set_im3,
			R.drawable.set_tae, R.drawable.set_gc, R.drawable.set_asm,
			R.drawable.set_ffsog, R.drawable.set_sog, R.drawable.set_bm,
			R.drawable.set_ffbm, R.drawable.set_tab, R.drawable.set_d10a,
			R.drawable.set_m10a, R.drawable.set_jl52, R.drawable.set_ffjl,
			R.drawable.set_ffcw, R.drawable.set_mcw, R.drawable.set_dkr,
			R.drawable.set_ffgsx, R.drawable.set_dwol, R.drawable.set_ams,
			R.drawable.set_avm, R.drawable.set_ffgga, R.drawable.set_mgg,
			R.drawable.set_mig, R.drawable.set_hulk, R.drawable.set_ffih,
			R.drawable.set_dsmff, R.drawable.set_dsm, 
			// Other
			R.drawable.set_ygo, R.drawable.set_ffs, R.drawable.set_dota2,
			R.drawable.set_mkr, R.drawable.set_t2t, R.drawable.set_hbtjlm,
			R.drawable.set_trek3, R.drawable.set_lr, R.drawable.set_ka2,
			R.drawable.set_bsi, R.drawable.set_pr, R.drawable.set_fotr,
			R.drawable.set_im, R.drawable.set_stmg, R.drawable.set_trek2,
			R.drawable.set_hbt, R.drawable.set_acb, R.drawable.set_acr,
			R.drawable.set_sdcc, R.drawable.set_sttat, R.drawable.set_trek,
			R.drawable.set_lotr, R.drawable.set_halo, R.drawable.set_gow,
			// Golden
			R.drawable.set_sf, R.drawable.set_dwmff, R.drawable.set_mca,
			R.drawable.set_mhtff, R.drawable.set_dglff, R.drawable.set_dglgf,
			R.drawable.set_mgxm, R.drawable.set_dan, R.drawable.set_dbd,
			R.drawable.set_mws, R.drawable.set_djh, R.drawable.set_dwm,
			R.drawable.set_dbn, R.drawable.set_dbb, R.drawable.set_dcl,
			R.drawable.set_mht, R.drawable.set_daa, R.drawable.set_msi,
			R.drawable.set_dba, R.drawable.set_dcr, R.drawable.set_mmu,
			R.drawable.set_djl, R.drawable.set_mav, R.drawable.set_dls,
			R.drawable.set_ihb, R.drawable.set_dor, R.drawable.set_m2099,
			R.drawable.set_msv, R.drawable.set_mdf, R.drawable.set_dgl,
			R.drawable.set_iiv, R.drawable.set_dgi, R.drawable.set_mdr,
			R.drawable.set_msn, R.drawable.set_dcd, R.drawable.set_maw,
			R.drawable.set_dio, R.drawable.set_icv, R.drawable.set_mff,
			R.drawable.set_dlg, R.drawable.set_ich, R.drawable.set_mmm,
			R.drawable.set_mul, R.drawable.set_dun, R.drawable.set_mui,
			R.drawable.set_mcm, R.drawable.set_iin, R.drawable.set_dcj,
			R.drawable.set_mxp, R.drawable.set_mct, R.drawable.set_dht,
			R.drawable.set_mic };

}
