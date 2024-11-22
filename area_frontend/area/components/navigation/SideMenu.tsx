import {useEffect, useState} from "react";
import Avatar from "@mui/material/Avatar";
import Drawer from '@mui/material/Drawer';
import Box from "@mui/material/Box";
import Divider from "@mui/material/Divider";
import Stack from "@mui/material/Stack";
import Typography from "@mui/material/Typography";
import backgroundImage from '@/public/background.jpg'
import NavMenuContext from '@/components/menu/NavMenuContent';
import LogoutRoundedIcon from '@mui/icons-material/LogoutRounded';
import { Link } from "@/i18n/routing";
import { getCookie } from "cookies-next";
import { Tooltip } from "@mui/material";

const drawerWidth = 240;

export default function SideMenu() {

    function getShortTitle(title : string, n = 15) {
        if (title) {
            return title.length > n ? title.substring(0, n) + '...' : title;
        } else {
            return 'Unknown'
        }
    }

    type ProfileType = {
        name: string;
        surname: string;
        email: string;
        password: string;
        phoneNumber: string;
        gender: string;
      };

    const [profile, setProfile] = useState<ProfileType>({
        name: "",
        surname: "",
        email: "",
        password: "",
        phoneNumber: "",
        gender: "",
      });

    const getProfileData = async () => {
        if (getCookie("userToken") === undefined || getCookie("clientId") === undefined ) {
            console.error("No cookies found");
            return;
        }
        try {
          const response = await fetch("http://localhost:8080/users/user", {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: String(getCookie("userToken")),
              UserId: String(getCookie("clientId")),
              RequestId: String(getCookie("clientId")),
            },
          });
    
          if (!response.ok) {
            console.error("Error while fetching", response);
            return;
          }
    
          const data = await response.json();
          setProfile(data);
        } catch (error) {
          console.error("Error", error);
        }
    };

    useEffect(() => {
        getProfileData();
    }, []);

    return (
        <Drawer
            sx={{
                display: { xs: 'none', md: 'block' },
                width: drawerWidth,
                flexShrink: 0,
                '& .MuiDrawer-paper': {
                    backgroundImage: `url(${backgroundImage.src})`,
                    backgroundPosition: 'center',
                    backgroundSize: 'cover',
                    // background: "rgba(255, 241, 240, 1)",
                    width: drawerWidth,
                    boxSizing: 'border-box',
                    boxShadow: "10px 0px 20px rgba(0,0,0,0.1);"
                },
            }}
            variant="permanent"
            anchor="left"
        >
            <Box
                sx={{
                    display: "flex",
                    p: 1.5,
                    justifyContent: "center"
                }}
            >
                <Typography variant="h4" component="h1" sx={{ color: 'white', fontWeight: 'bold' }}>
                    AREA
                </Typography>
            </Box>
            <Divider sx={{ bgcolor: "white"}} />
            <NavMenuContext />
            <div
                style={{
                    marginTop: "auto",
                    width: "100%"
                }}
            >
                <Divider sx={{ bgcolor: "white"}} />
                <Stack
                    direction="row"
                    sx={{
                        p: 2,
                        gap: 1,
                        alignItems: "center",
                        borderTop: "1px solid",
                        borderColor: "divider",
                    }}
                >
                    <Avatar
                        sizes="small"
                        alt={`${profile.name} ${profile.surname}`}
                        sx={{ width: 36, height: 36 ,bgcolor: "#f6466b" }}
                    >
                        {String(profile.name[0]).toUpperCase()}{String(profile.surname[0]).toUpperCase()}
                    </Avatar>
                    <Box sx={{ mr: "auto" }}>
                        <Typography
                            variant="body2"
                            sx={{ color: 'white', fontWeight: 500, lineHeight: "16px" }}
                        >
                            {`${profile.name} ${profile.surname}`}
                        </Typography>
                        <Tooltip title={profile.email} placement="top-start">
                            <Typography variant="caption" sx={{ color: 'white' }}>
                                {getShortTitle(profile.email)}
                            </Typography>
                        </Tooltip>
                    </Box>
                    <Link href="/logout">
                        <LogoutRoundedIcon className="text-white cursor-pointer"/>
                    </Link>
                </Stack>
            </div>
        </Drawer>
    );
}
