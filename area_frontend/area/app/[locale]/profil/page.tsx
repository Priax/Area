"use client";

import { useTranslations } from "next-intl";
import { useEffect, useState } from "react";
import { getCookie } from "cookies-next";
import { useRouter } from "next/navigation";
import Typography from "@mui/material/Typography";
import {
  Stack,
  TextField,
  InputLabel,
  Select,
  Button,
  MenuItem,
  FormControl,
} from "@mui/material";

type ProfileType = {
  id: number;
  name: string;
  surname: string;
  email: string;
  password: string;
  phoneNumber: string;
  gender: string;
  dateOfBirth: string;
  role: string;
};

export default function Profile() {
  const t = useTranslations("ProfilePage");
  const router = useRouter();

  const [profile, setProfile] = useState<ProfileType>({
    id: 0,
    name: "",
    surname: "",
    email: "",
    password: "",
    phoneNumber: "",
    gender: "",
    dateOfBirth: "",
    role: "",
  });
  const [gender, setGender] = useState("");

  const handleChange = (event: any) => {
    setGender(event.target.value as string);
  };

  const updateProfile = async () => {

    console.log("Sending", JSON.stringify({
      id: Number(getCookie("clientId")),
      name: profile.name,
      surname: profile.surname,
      email: profile.email,
      phoneNumber: profile.phoneNumber,
      dateOfBirth: profile.dateOfBirth,
      gender: profile.gender,
      role: profile.role,

    }))

    try {
      const response = await fetch("http://localhost:8080/users/update", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Authorization": String(getCookie("userToken")),
          "UserID": String(getCookie("clientId")),
        },
        body: JSON.stringify({
          id: Number(getCookie("clientId")),
          name: profile.name,
          surname: profile.surname,
          email: profile.email,
          gender: profile.gender,
          role: profile.role,
          dateOfBirth: profile.dateOfBirth,
          phoneNumber: profile.phoneNumber,
        })
      });
  
    if (!response.ok) {
      console.error("Error while fetching", response);
      return;
    }
  
    const data = await response.json();
    console.log("Data:", data);
    location.reload();
  } catch (error) {
    console.error("Error", error);
  }
}

  useEffect(() => {
    const userToken = getCookie("userToken");
    const userId = getCookie("clientId");

    if (userToken === undefined && userId === undefined) {
      const currentLocale = getCookie("NEXT_LOCALE") || "en";
      router.push(`/${currentLocale}/login`);
    }
  }, [router]);

  useEffect(() => {
    let isMounted = true;
    const fetchProfileData = async () => {
      const response = await fetch("http://localhost:8080/credentials/id", {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": String(getCookie("userToken")),
          "UserID": String(getCookie("clientId")),
          "X-Request-ID": String(getCookie("clientId")),
        },
      });
  
      if (response.ok) {
        const data = await response.json();
        if (isMounted && data && data.user) {
          setProfile(data.user);
          console.log("profile fetched", data.user);
        }
      } else {
        console.error("Error while fetching profile data:", response);
      }
    };
  
    fetchProfileData();
  
    return () => {
      isMounted = false;
    };
  }, []);

  useEffect(() => {
    setGender(profile.gender);
  }, [profile]);

  return (
    <div>
      <Typography component="h2" variant="h6" sx={{ mb: 2, mt: 1, pb: 3 }} className="text-2xl">
        {t("title")}
      </Typography>
      <div
        style={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          height: "70vh",
          }}
      >
        <Stack
          sx={{
            p: 4,
            gap: 2,
            borderTop: "1px solid",
            borderColor: "divider",
            bgcolor: 'white',
            justifyContent: 'center',
            alignItems: "center",
            width: 350,
            borderRadius: 2,
          }}
        >
          <TextField
            label={t("name")}
            value={profile.name}
            sx={{ width: 300 }}
            onChange={(e) => setProfile({ ...profile, name: e.target.value })}
          />
          <TextField
            label={t("surname")}
            value={profile.surname}
            sx={{ width: 300 }}
            onChange={(e) => setProfile({ ...profile, surname: e.target.value })}
          />
          <TextField
            label={t("email")}
            value={profile.email}
            sx={{ width: 300 }}
            onChange={(e) => setProfile({ ...profile, email: e.target.value })}
          />
          <TextField
            type="password"
            label={t("password")}
            value="*******"
            sx={{ width: 300 }}
            onChange={(e) => setProfile({ ...profile, password: e.target.value })}
          />
          <TextField
            type="number"
            label={t("phonenumber")}
            value={profile.phoneNumber}
            sx={{ width: 300 }}
            onChange={(e) => setProfile({ ...profile, phoneNumber: e.target.value })}
          />
          <FormControl sx={{ width: 300 }}>
            <InputLabel id="gender">{t("gender")}</InputLabel>
            <Select
              labelId="gender"
              value={gender}
              onChange={(e) => {
                handleChange(e);
                setProfile({ ...profile, gender: e.target.value });
              }}
              label={t("gender")}
            >
              <MenuItem value={"male"}>{t("male")}</MenuItem>
              <MenuItem value={"female"}>{t("female")}</MenuItem>
              <MenuItem value={"Other"}>{t("other")}</MenuItem>
            </Select>
          </FormControl>
          <Button
            variant="contained"
            sx={{ width: 300 }}
            onClick={updateProfile}
          >
            {t("edit")}
          </Button>
        </Stack>
      </div>
    </div>
);
}
