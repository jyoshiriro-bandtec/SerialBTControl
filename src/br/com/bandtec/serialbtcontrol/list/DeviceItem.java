//
//    Serial BT Control
//    Copyright (c) 2014 Carlos Rafael Gimenes das Neves
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program. If not, see {http://www.gnu.org/licenses/}.
//
//    https://github.com/BandTec/SerialBTControl
//
package br.com.bandtec.serialbtcontrol.list;

public class DeviceItem extends BaseItem {
	public final String description, address;
	public final boolean paired;
	
	public DeviceItem(String description, String address, boolean paired, boolean recentlyUsed) {
		super(recentlyUsed);
		this.description = description;
		this.address = address;
		this.paired = paired;
	}
	
	@Override
	public String toString() {
		return description;
	}
}
