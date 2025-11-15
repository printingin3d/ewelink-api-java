package com.github.realzimboguy.ewelink.api.model.home;

import com.github.realzimboguy.ewelink.api.model.DeviceStatus;
import com.google.gson.annotations.SerializedName;

public class OutletSwitch {
	private int outlet;
	@SerializedName("switch")
	private String _switch;

	public OutletSwitch() {}
	
	public OutletSwitch(int outlet, String _switch) {
        this.outlet = outlet;
        this._switch = _switch;
    }
    public int getOutlet() {
        return outlet;
    }
    public DeviceStatus getDeviceStatus() {
        return DeviceStatus.fromString(_switch);
    }

    @Override
	public String toString() {

		return "OutletSwitch{" +
				"outlet=" + outlet +
				", _switch='" + _switch + '\'' +
				'}';
	}
}
