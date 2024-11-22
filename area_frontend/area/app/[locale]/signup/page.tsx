"use client";

import { useTranslations } from 'next-intl';
import backgroundImage from '@/public/background.jpg'
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import Divider from '@mui/material/Divider';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormLabel from '@mui/material/FormLabel';
import FormControl from '@mui/material/FormControl';
import { Link } from '@/i18n/routing';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import Stack from '@mui/material/Stack';
import Card from '@mui/material/Card';
import GoogleIcon from '@mui/icons-material/Google';
import PhoneInput from 'react-phone-input-2'
// import 'react-phone-input-2/lib/material.css'
import LocaleSwitcherAuth from '@/components/translation/LocaleSwitcherAuth';
import Select, { SelectChangeEvent } from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import InputLabel from '@mui/material/InputLabel';
import '@/public/css/material.css'
import { useState } from 'react';
import { useEffect } from 'react';

import { setCookie } from 'cookies-next';
import { getCookie } from 'cookies-next';
import { useRouter } from 'next/navigation';
import LoginGoogleButton from '@/components/googleSignIn/googleButton';

export default function Login() {
    const t = useTranslations('Signup');

    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [dateOfBirth, setDateOfBirth] = useState('');
    const [phone, setPhone] = useState('');
    const [email, setEmail] = useState('');
    const [gender, setGender] = useState('Man');

    const [password, setPassword] = useState('');
    const [passwordConfirm, setPasswordConfirm] = useState('');
    const router = useRouter();

    const pref_countries = ["fr"];

    const handleChangeGender = (event: SelectChangeEvent) => {
        setGender(event.target.value as string);
    };

    useEffect(() => {
        const userToken = getCookie("userToken");
        const userId = getCookie("clientId");

        if (userToken !== undefined && userId !== undefined) {
            const currentLocale = getCookie("NEXT_LOCALE") || "en";
            router.push(`/${currentLocale}`);
        }
    }, []);

    const sendData = async (event: any) => {

        if (password.length > 5 && password === passwordConfirm) {
            try {
                const response = await fetch('http://localhost:8080/auth/signUp', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        email: email,
                        password: password,
                        dateOfBirth: dateOfBirth,
                        gender: gender,
                        name: firstName,
                        surname: lastName,
                        phoneNumber: phone,
                        role: 0
                    }),
                });

                if (!response.ok) {
                    console.error("error", response)
                    return;
                }

                const data = await response.json();

                if (getCookie("userToken") !== undefined) {
                    console.log("userToken already set");
                    return;
                }
                setCookie("userToken", data['right']);
                setCookie("clientId", data['left']);

                const currentLocale = getCookie("NEXT_LOCALE") || "en";
                router.push(`/${currentLocale}`);

            } catch (error) {
                console.error(error);
            }
        } else {
            alert("passwords aren't equal, reseting...")
        }
    };

    return (
        <div style={{
            backgroundImage: `url(${backgroundImage.src})`,
            backgroundPosition: 'center',
            backgroundSize: 'cover'
        }}>
            <Stack
                sx={{
                    justifyContent: 'center',
                    height: '100vh',
                    p: 2,
                }}
            >
                <div className='flex justify-center'>
                    <Card className='p-10 mx-5 lg:mx-0 w-full lg:w-1/2 bg-white bg-opacity-70 rounded-3xl backdrop-blur-md' variant="outlined">
                        <Stack direction="row" className='mb-5'
                            sx={{
                                justifyContent: "space-between",
                                alignItems: "center",
                            }}
                        >
                            <Typography
                                className='font-extrabold text-center'
                                component="h1"
                                variant="h4"
                                sx={{ width: 'auto', fontSize: 'clamp(2rem, 10vw, 2.15rem)', color: "#f6466b" }}
                            >
                                {t('title')}
                            </Typography>
                            <LocaleSwitcherAuth />
                        </Stack>
                        <Box
                            component="form"
                            sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}
                            onSubmit={(event) => {
                                event.preventDefault();
                                sendData(event)
                            }}
                        >
                            <FormControl>
                                <TextField
                                    required
                                    fullWidth
                                    id="firstname"
                                    label={t('first_name')}
                                    placeholder={t('your_first_name')}
                                    name="first_name"
                                    variant="outlined"
                                    autoFocus
                                    onChange={(e) => setFirstName(e.target.value)}
                                />
                            </FormControl>
                            <FormControl>
                                <TextField
                                    required
                                    fullWidth
                                    id="lastname"
                                    label={t('last_name')}
                                    placeholder={t('your_last_name')}
                                    name="last_name"
                                    variant="outlined"
                                    onChange={(e) => setLastName(e.target.value)}
                                />
                            </FormControl>
                            <FormControl>
                                <TextField
                                    required
                                    fullWidth
                                    id="date_of_birth"
                                    label={t('date_of_birth')}
                                    name="date_of_birth"
                                    type="date"
                                    variant="outlined"
                                    onChange={(e) => setDateOfBirth(e.target.value)}
                                />
                            </FormControl>
                            <FormControl required>
                                <PhoneInput
                                    country={'fr'}
                                    value={phone}
                                    onChange={phoneForm => setPhone(phoneForm)}
                                    inputProps={{
                                        name: 'phone',
                                        required: true,
                                        autoFocus: true,
                                    }}
                                    specialLabel=""
                                    preferredCountries={pref_countries}
                                    enableSearch={true}
                                    placeholder='+33 6 02 43 42 33'
                                />
                            </FormControl>
                            <FormControl>
                                <InputLabel id="demo-simple-select-label">{t('gender')}</InputLabel>
                                <Select
                                    required
                                    fullWidth
                                    labelId="demo-simple-select-label"
                                    id="demo-simple-select"
                                    value={gender}
                                    label={t('gender')}
                                    defaultValue="Man"
                                    onChange={handleChangeGender}
                                >
                                    <MenuItem value="Man">{t('man')}</MenuItem>
                                    <MenuItem value="Woman">{t('woman')}</MenuItem>
                                    <MenuItem value="Other">{t('other')}</MenuItem>
                                </Select>
                            </FormControl>
                            <FormControl>
                                <TextField
                                    required
                                    fullWidth
                                    id="gender"
                                    placeholder={t('your_email')}
                                    label="Email"
                                    name="email"
                                    variant="outlined"
                                    onChange={(e) => setEmail(e.target.value)}
                                />
                            </FormControl>
                            <FormControl>
                                <TextField
                                    required
                                    fullWidth
                                    name="password"
                                    placeholder={t('your_password')}
                                    label={t('password')}
                                    type="password"
                                    id="password"
                                    variant="outlined"
                                    onChange={(e) => setPassword(e.target.value)}
                                />
                            </FormControl>
                            <FormControl>
                                <TextField
                                    required
                                    fullWidth
                                    name="comfirm_password"
                                    placeholder={t('confirm_your_password')}
                                    label={t('confirm_password')}
                                    type="password"
                                    id="comfirm_password"
                                    variant="outlined"
                                    onChange={(e) => setPasswordConfirm(e.target.value)}
                                />
                            </FormControl>
                            <Button
                                type="submit"
                                fullWidth
                                variant="contained"
                            >
                                {t('title')}
                            </Button>
                            <Typography sx={{ textAlign: 'center' }}>
                                {t('registered')}?{' '}
                                <span>
                                    <Link
                                        className='text-blue-600 visited:text-purple-600'
                                        href="/login"
                                    >
                                        {t('login')}
                                    </Link>
                                </span>
                            </Typography>
                        </Box>
                    </Card>
                </div>
            </Stack>
        </div>
    );
}