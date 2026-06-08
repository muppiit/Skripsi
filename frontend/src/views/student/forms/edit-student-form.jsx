/* eslint-disable no-unused-vars */
/* eslint-disable react/prop-types */
import React, { useEffect, useState } from "react";
import { Form, Input, Select, Modal, DatePicker, message } from "antd";
import dayjs from "dayjs";
import { getKelas } from "@/api/kelas";
import { getReligions } from "@/api/religion";
import { getStudyPrograms } from "@/api/studyProgram";
import { getUsers } from "@/api/user";
import { getStudents } from "@/api/student";

const { TextArea } = Input;
const { Option } = Select;

const cleanStudentPayload = (values) => ({
  ...values,
  id: values.id?.toString?.().trim?.() || undefined,
  user_id: values.user_id?.toString?.(),
  nisn: values.nisn?.toString?.(),
  name: values.name?.toString?.(),
  gender: values.gender?.toString?.(),
  phone: values.phone?.toString?.(),
  religion_id: values.religion_id?.toString?.(),
  idStudyProgram: values.idStudyProgram?.toString?.(),
  idKelas: values.idKelas?.toString?.(),
  idTahunAjaran: values.idTahunAjaran?.toString?.(),
  birth_date: values.birth_date?.format
    ? values.birth_date.format("YYYY-MM-DD")
    : values.birth_date?.toString?.(),
  place_born: values.place_born?.toString?.(),
  address: values.address?.toString?.(),
});

const EditStudentForm = ({
  visible,
  onCancel,
  onOk,
  confirmLoading,
  currentRowData,
}) => {
  const [form] = Form.useForm();
  const selectedUserId = Form.useWatch("user_id", form);
  const [kelasList, setKelasList] = useState([]);
  const [filteredKelasList, setFilteredKelasList] = useState([]);
  const [studyProgramList, setStudyProgramList] = useState([]);
  const [religionList, setReligionList] = useState([]);
  const [userList, setUserList] = useState([]);

  const isStudentRole = (role) => {
    const normalizedRole = role?.toString().toLowerCase();
    return (
      normalizedRole === "5" ||
      normalizedRole === "role_student" ||
      normalizedRole === "student" ||
      normalizedRole === "mahasiswa"
    );
  };

  const getUserStudyProgramId = (user) =>
    user?.school?.idSchool ||
    user?.study_program?.idSchool ||
    user?.school?.id ||
    user?.study_program?.id ||
    user?.school_id;

  const getStudyProgramId = (student) =>
    student?.studyProgram?.id || student?.study_program?.id;

  const getTahunAjaranId = (student) =>
    student?.tahunAjaran?.idTahun || student?.tahunAjaran?.id;

  const getTahunAjaranName = (student) =>
    student?.tahunAjaran?.tahunAjaran || student?.tahunAjaran?.name;

  const filterKelasByStudyProgram = (list, studyProgramId) =>
    (list || []).filter((kelas) => {
      const kelasStudyProgramId =
        kelas?.studyProgram?.id || kelas?.study_program?.id;
      return !kelasStudyProgramId || kelasStudyProgramId === studyProgramId;
    });

  const fetchData = async () => {
    try {
      const [religionRes, programRes, usersRes, studentsRes] = await Promise.all([
        getReligions(),
        getStudyPrograms(),
        getUsers(),
        getStudents(),
      ]);

      setReligionList(
        religionRes.data.statusCode === 200 ? religionRes.data.content || [] : []
      );
      setStudyProgramList(
        programRes.data.statusCode === 200 ? programRes.data.content || [] : []
      );
      const nextUserList =
        usersRes.data.statusCode === 200
          ? (usersRes.data.content || []).filter((user) => {
              const linkedUserIds = new Set(
                studentsRes.data.statusCode === 200
                  ? (studentsRes.data.content || [])
                      .filter((student) => student.id !== currentRowData?.id)
                      .map((student) => student.user_id)
                      .filter(Boolean)
                  : []
              );
              return (
                isStudentRole(user.roles) &&
                !linkedUserIds.has(user.id) &&
                !!getUserStudyProgramId(user)
              );
            })
          : [];
      setUserList(nextUserList);

      const selectedUser = nextUserList.find(
        (user) => user.id === currentRowData?.user_id
      );
      const studyProgramId =
        getUserStudyProgramId(selectedUser) ||
        getStudyProgramId(currentRowData);

      const kelasRes = studyProgramId ? await getKelas(studyProgramId) : await getKelas();
      const nextKelasList =
        kelasRes.data.statusCode === 200 ? kelasRes.data.content || [] : [];
      setKelasList(nextKelasList);
      setFilteredKelasList(filterKelasByStudyProgram(nextKelasList, studyProgramId));
      if (studyProgramId) {
        form.setFieldsValue({ idStudyProgram: studyProgramId });
      }
    } catch (error) {
      message.error("Gagal memuat data: " + error.message);
    }
  };

  useEffect(() => {
    if (!visible) return;

    fetchData();
    form.setFieldsValue({
      id: currentRowData?.id,
      user_id: currentRowData?.user_id,
      nisn: currentRowData?.nisn,
      name: currentRowData?.name,
      place_born: currentRowData?.place_born,
      birth_date: currentRowData?.birth_date
        ? dayjs(currentRowData.birth_date)
        : undefined,
      gender: currentRowData?.gender,
      phone: currentRowData?.phone,
      address: currentRowData?.address,
      religion_id: currentRowData?.religion?.id,
      idStudyProgram: getStudyProgramId(currentRowData),
      idKelas: currentRowData?.kelas?.idKelas,
      idTahunAjaran: getTahunAjaranId(currentRowData),
      tahunAjaranDisplay: getTahunAjaranName(currentRowData),
    });
  }, [visible, currentRowData, form]);

  const handleStudyProgramChange = (studyProgramId) => {
    setFilteredKelasList(filterKelasByStudyProgram(kelasList, studyProgramId));
    form.setFieldsValue({
      idKelas: undefined,
      idTahunAjaran: undefined,
      tahunAjaranDisplay: undefined,
    });
  };

  const handleUserChange = async (userId) => {
    const selectedUser = (userList || []).find((user) => user.id === userId);
    const studyProgramId = getUserStudyProgramId(selectedUser);

    if (studyProgramId) {
      form.setFieldsValue({ idStudyProgram: studyProgramId });
      form.setFieldsValue({
        idKelas: undefined,
        idTahunAjaran: undefined,
        tahunAjaranDisplay: undefined,
      });
      try {
        const kelasRes = await getKelas(studyProgramId);
        const nextKelasList =
          kelasRes.data.statusCode === 200 ? kelasRes.data.content || [] : [];
        setKelasList(nextKelasList);
        setFilteredKelasList(filterKelasByStudyProgram(nextKelasList, studyProgramId));
      } catch (error) {
        message.error("Gagal memuat kelas: " + error.message);
      }
    } else {
      form.setFieldsValue({ idStudyProgram: undefined, idKelas: undefined });
      setFilteredKelasList([]);
      message.warning("User login ini belum memiliki Prodi. Edit data user terlebih dahulu.");
    }
  };

  const handleKelasChange = (kelasId) => {
    const selectedKelas = (filteredKelasList || []).find(
      (kelas) => kelas.idKelas === kelasId
    );

    if (selectedKelas?.tahunAjaran) {
      form.setFieldsValue({
        idTahunAjaran: selectedKelas.tahunAjaran.idTahun,
        tahunAjaranDisplay: selectedKelas.tahunAjaran.tahunAjaran,
      });
    }
  };

  const handleSubmit = () => {
    form
      .validateFields()
      .then((values) => {
        onOk(cleanStudentPayload(values));
      })
      .catch((info) => {
        console.log("Validate Failed:", info);
      });
  };

  const handleCancel = () => {
    form.resetFields();
    onCancel();
  };

  const formItemLayout = {
    labelCol: { xs: { span: 24 }, sm: { span: 8 } },
    wrapperCol: { xs: { span: 24 }, sm: { span: 16 } },
  };

  return (
    <Modal
      title="Edit Mahasiswa"
      open={visible}
      onCancel={handleCancel}
      onOk={handleSubmit}
      confirmLoading={confirmLoading}
      okText="Simpan"
      cancelText="Batal"
    >
      <Form form={form} {...formItemLayout}>
        <Form.Item label="ID Mahasiswa:" name="id">
          <Input disabled />
        </Form.Item>

        <Form.Item
          label="NISN:"
          name="nisn"
          rules={[{ required: true, message: "NISN wajib diisi" }]}
        >
          <Input placeholder="NISN" />
        </Form.Item>

        <Form.Item
          label="User Login:"
          name="user_id"
          rules={[{ required: true, message: "Silahkan pilih user login mahasiswa" }]}
        >
          <Select
            placeholder="Pilih User Login Mahasiswa"
            showSearch
            optionFilterProp="children"
            onChange={handleUserChange}
          >
            {userList.map((user) => (
              <Option key={user.id} value={user.id}>
                {(user.username || user.id) + " - " + (user.name || "-")}
              </Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Nama Lengkap:"
          name="name"
          rules={[{ required: true, message: "Nama lengkap mahasiswa wajib diisi" }]}
        >
          <Input placeholder="Nama Lengkap Mahasiswa" />
        </Form.Item>

        <Form.Item
          label="Tempat Lahir:"
          name="place_born"
          rules={[{ required: true, message: "Tempat lahir wajib diisi" }]}
        >
          <Input placeholder="Tempat Lahir" />
        </Form.Item>

        <Form.Item
          label="Tanggal Lahir:"
          name="birth_date"
          rules={[{ required: true, message: "Tanggal lahir wajib diisi" }]}
        >
          <DatePicker placeholder="Tanggal Lahir" style={{ width: "100%" }} />
        </Form.Item>

        <Form.Item
          label="Gender:"
          name="gender"
          rules={[{ required: true, message: "Gender wajib diisi" }]}
        >
          <Select placeholder="Gender">
            <Option value="L">Laki-laki</Option>
            <Option value="P">Perempuan</Option>
          </Select>
        </Form.Item>

        <Form.Item
          label="Nomor Telepon:"
          name="phone"
          rules={[{ required: true, message: "Nomor telepon wajib diisi" }]}
        >
          <Input placeholder="Nomor Telepon" />
        </Form.Item>

        <Form.Item
          label="Alamat:"
          name="address"
          rules={[{ required: true, message: "Alamat wajib diisi" }]}
        >
          <TextArea placeholder="Alamat" />
        </Form.Item>

        <Form.Item
          label="Agama:"
          name="religion_id"
          rules={[{ required: true, message: "Silahkan pilih agama" }]}
        >
          <Select placeholder="Pilih Agama" showSearch optionFilterProp="children">
            {religionList.map((religion) => (
              <Option value={religion.id} key={religion.id}>
                {religion.name}
              </Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Program Studi:"
          name="idStudyProgram"
          rules={[{ required: true, message: "Silahkan pilih program studi" }]}
        >
          <Select
            placeholder="Pilih Program Studi"
            onChange={handleStudyProgramChange}
            showSearch
            optionFilterProp="children"
            disabled={!!selectedUserId}
          >
            {studyProgramList.map((program) => (
              <Option key={program.id} value={program.id}>
                {program.name}
              </Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          label="Kelas:"
          name="idKelas"
          rules={[{ required: true, message: "Silahkan pilih kelas" }]}
        >
          <Select
            placeholder="Pilih Kelas"
            onChange={handleKelasChange}
            showSearch
            optionFilterProp="children"
          >
            {filteredKelasList.map((kelas) => (
              <Option key={kelas.idKelas} value={kelas.idKelas}>
                {kelas.namaKelas}
              </Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item name="idTahunAjaran" hidden>
          <Input />
        </Form.Item>

        <Form.Item label="Tahun Ajaran:" name="tahunAjaranDisplay">
          <Input placeholder="Otomatis terisi dari Kelas" disabled />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default EditStudentForm;
