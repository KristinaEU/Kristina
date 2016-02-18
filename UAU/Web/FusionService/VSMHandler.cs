using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace FusionService
{
    public class VSMHandler
    {

        public static StatusCode getStatus()
        {
            var url = "http://137.250.171.231:11223/?cmd=status";
            // Synchronous Consumption
            var syncClient = new TimeoutWebClient();
            syncClient.Timeout = (1000);
            string status = "";
            StatusCode ret = StatusCode.UNKNOWN;
            try
            {
                string answer = syncClient.DownloadString(url);
                status = XMLParser.parseVSM(answer);
                if (status.Equals("ACTIVE"))
                    return StatusCode.UP;
                else
                    return StatusCode.DOWN;
            }
            catch (Exception e)
            {
               return StatusCode.UNKNOWN;
            }

            return ret;
        }


        public static String executeCommand(string cmd, string arg)
        {

            var url = "http://137.250.171.231:11223/?cmd=" + cmd + "&arg=" + arg;
            // Synchronous Consumption
            var syncClient = new TimeoutWebClient();
            syncClient.Timeout = (1000);
            try
            {
                return syncClient.DownloadString(url);
            }
            catch (Exception e)
            {
                return e.Message;
            }
            return null;
        }


        public class TimeoutWebClient : System.Net.WebClient
        {
            public int Timeout { get; set; }

            public TimeoutWebClient()
            {
                Timeout = 1000;
            }

            public TimeoutWebClient(int timeout)
            {
                Timeout = timeout;
            }

            protected override System.Net.WebRequest GetWebRequest(Uri address)
            {
                System.Net.WebRequest request = base.GetWebRequest(address);
                request.Timeout = Timeout;
                return request;
            }
        }
    }
}
