package com.ws.common.kit.intenet;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

/**
 * @author wangshuo
 * @version 2018-03-15
 */
public class InetUtil {

    private static Splitter DOT_SPLITTER = Splitter.on(".");

    /**
     * 针对只有一个网卡的设备
     * @return 返回设备的ip
     */
    public static String getLocalhost() {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }

    /**
     * 根据目标网络号，查询属于该网络号的本机ip
     *
     * @param targetNetNum  目标网络号
     * @return  返回符合要求的本机ip
     */
    public static String getLocalIp(String targetNetNum) {
        checkArgument(!Strings.isNullOrEmpty(targetNetNum), "targetNetNum can not be null");
        try {
            Enumeration netInterfaces;
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
                Enumeration nii = ni.getInetAddresses();

                while (nii.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) nii.nextElement();
                    if (checkIsTargetIp(targetNetNum, inetAddress)) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return getLocalhost();
    }

    /**
     * 获取子网掩码的长度
     */
    private static int getMaskLength(InetAddress inetAddress) {
        try {
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
            List<InterfaceAddress> list = networkInterface.getInterfaceAddresses();
            for (InterfaceAddress address : list) {
                if (address.getAddress() instanceof Inet4Address) {
                    return address.getNetworkPrefixLength();
                }
            }
        } catch (Exception e) {
            return 31;
        }
        return 31;
    }

    /**
     * 获取网络号
     */
    private static String getNetworkNumber(String hostIp, int maskLength) {

        List<String> ips = newArrayList(DOT_SPLITTER.split(hostIp));
        long ipAddr = (Long.parseLong(ips.get(0)) << 24) | (Long.parseLong(ips.get(1)) << 16) | (
                Long.parseLong(ips.get(2)) << 8) | Long.parseLong(ips.get(3));

        long mask = 0x0FFFFFFFFL & (0xFFFFFFFF << (32 - maskLength));
        long networkNumber = ipAddr & mask;
        long[] networkNums = new long[4];

        networkNums[0] = (networkNumber & 0x0FF000000L) >> 24;
        networkNums[1] = (networkNumber & 0x0FF0000L) >> 16;
        networkNums[2] = (networkNumber & 0x0FF00L) >> 8;
        networkNums[3] = networkNumber & 0x0FFL;

        return networkNums[0] + "." + networkNums[1] + "." + networkNums[2] + "." + networkNums[3];
    }

    private static boolean checkIsTargetIp(String targetNetNum, InetAddress inetAddress) {
        if (!inetAddress.getHostAddress().contains(":")) {
            int maskLen = getMaskLength(inetAddress);
            String networkNum = getNetworkNumber(inetAddress.getHostAddress(), maskLen);
            if (targetNetNum.equals(networkNum)) {
                return true;
            }
        }
        return false;
    }
}
