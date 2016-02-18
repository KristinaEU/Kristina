using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Web;
using System.Xml;

namespace FusionService
{
    public class XMLParser
    {
        public static SSIEvent parseEvent(string xml)
        {
            SSIEvent ret = new SSIEvent("", 0, 0);
            using (XmlReader reader = XmlReader.Create(new StringReader(xml)))
            {
                reader.ReadToFollowing("event");

                string ev = reader.GetAttribute("event");
                string sender = reader.GetAttribute("sender");

                //If the event is only for the activity-indication
                if (ev.Equals("activity") && sender.Equals("stream"))
                {
                    ret.Origin = "ACTIVITY";
                    try
                    {
                        ret.EventType = reader.GetAttribute("state");
                        return ret;
                    }
                    catch (Exception ex)
                    {
                    }
                }
                //event indicates wether the pipe is running or not
                if (ev.Equals("pipe") && sender.Equals("heartbeat"))
                {
                    ret.Origin = "HEARTBEAT";
                    return ret;
                }

                //If the event contains emotion-information
                if (sender.Equals("shore"))
                {
                    ret.Origin = "MIMIC";
                }
                else if (sender.Equals("audio"))
                {
                    ret.Origin = "PROSODY";
                }
                else if (sender.Equals("gesture"))
                {
                    ret.Origin = "GESTURE";
                }
                else if (sender.Equals("fsender"))
                {
                    ret.Origin = "FUSION";
                }

                try
                {
                    reader.ReadToDescendant("tuple");
                    ret.Valence = Double.Parse(reader.GetAttribute("value"), System.Globalization.CultureInfo.InvariantCulture);
                }
                catch (Exception ex)
                {
                    ret.Valence = 0;
                }
                try
                {
                    reader.ReadToNextSibling("tuple");
                    ret.Arousal = Double.Parse(reader.GetAttribute("value"), System.Globalization.CultureInfo.InvariantCulture);
                }
                catch (Exception ex)
                {
                    ret.Arousal = 0;
                }

            }

            return ret;
        }

        public static string parseVSM(string xml)
        {
            using (XmlReader reader = XmlReader.Create(new StringReader(xml)))
            {
                reader.ReadToFollowing("Result");

                string ev = reader.ReadElementContentAsString();
                ev = ev.Replace("\n", "");
                return ev;

            }
        }

    }
}