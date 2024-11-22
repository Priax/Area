"use client"

import { useState, useEffect } from "react";
import { getCookie } from 'cookies-next';
import { useRouter } from 'next/navigation';

import { useTranslations } from "next-intl";
import Typography from '@mui/material/Typography';
import DeleteIcon from '@mui/icons-material/Delete';
import { Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow } from "@mui/material";

export default function admin() {

    const router = useRouter();
    const [data, setData] = useState<UserData[]>([]);

    interface UserData {
        id: number;
        name: string;
        surname: string;
        email: string;
        gender: string;
        role: string;
        dateOfBirth: string;
        phoneNumber: string;
    }    

    const deleteUser = async (idUser : number) => {

        if (idUser === 1) {
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/users/delete', {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'UserID': String(getCookie('clientId')),
                    'Authorization': 'Bearer ' + getCookie('userToken'),
                    "X-Request-ID": String(idUser),
                }
            })

            if (!response.ok) {
                console.error("Error while fetching", response);
            }

            const data = await response.text();
            console.log("Data :", data);
            location.reload();
        } catch (error) {
            console.error("Error", error);
        }
    }

    const getUsers = async () => {
        try {
            const response = await fetch ('http://localhost:8080/users/', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'UserID': String(getCookie('clientId')),
                    'Authorization': 'Bearer ' + getCookie('userToken'),
                }
            })
            
            if (!response.ok) {
                console.log("Error when fetching", response);
                const currentLocale = getCookie("NEXT_LOCALE") || "en";
                router.push(`/${currentLocale}/`);
                return
            }
            
            const data = await response.json();
            const transformedData = data.map((item: any): UserData => ({
                id: item.id,
                name: item.name,
                surname: item.surname,
                email: item.email,
                gender: item.gender,
                role: item.role,
                dateOfBirth: item.dateOfBirth,
                phoneNumber: item.phoneNumber
            }));

            setData(transformedData);
            console.log("All users", JSON.stringify(transformedData));
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
        getUsers();
    }, []);

    const t = useTranslations("AdminPage");

    return (
        <>
        <Typography component="h2" variant="h6" sx={{ mb: 2 }}>
            {t("title")}
        </Typography>
            <TableContainer component={Paper} className="shadow-lg rounded-lg mt-6">
                <Table>
                    <TableHead className="bg-gradient-to-r from-pink-700 to-purple-600">
                        <TableRow>
                            <TableCell className="text-white font-bold">ID</TableCell>
                            <TableCell className="text-white font-bold">Name</TableCell>
                            <TableCell className="text-white font-bold">Surname</TableCell>
                            <TableCell className="text-white font-bold">Email</TableCell>
                            <TableCell className="text-white font-bold">Gender</TableCell>
                            <TableCell className="text-white font-bold">Role</TableCell>
                            <TableCell className="text-white font-bold">Date of Birth</TableCell>
                            <TableCell className="text-white font-bold">Phone number</TableCell>
                            <TableCell className="text-white font-bold"></TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {data.map((item) => (
                            <TableRow key={item.id} className="hover:bg-blue-50">
                                <TableCell className="border-b border-gray-200">{item.id}</TableCell>
                                <TableCell className="border-b border-gray-200">{item.name}</TableCell>
                                <TableCell className="border-b border-gray-200">{item.surname}</TableCell>
                                <TableCell className="border-b border-gray-200">{item.email}</TableCell>
                                <TableCell className="border-b border-gray-200">{item.gender}</TableCell>
                                <TableCell className="border-b border-gray-200">{item.role}</TableCell>
                                <TableCell className="border-b border-gray-200">{item.dateOfBirth}</TableCell>
                                <TableCell className="border-b border-gray-200">{item.phoneNumber}</TableCell>
                                <TableCell className="border-b border-gray-200">
                                    <button
                                        onClick={() => {deleteUser(item.id)}}
                                        className="rounded hover:bg-gray-100 transition-colors"
                                        aria-label="Delete"
                                        >
                                        <DeleteIcon sx={{color: '#D05353'}} />
                                    </button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </>
    )
}
